package com.overpass.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overpass.common.Constants.Status;
import com.overpass.common.Constants.StatusLight;
import com.overpass.common.Utils;
import com.overpass.model.Dashboard;
import com.overpass.model.GroupOverpass;
import com.overpass.model.MessageToNotify;
import com.overpass.model.Overpass;
import com.overpass.model.OverpassStatus;
import com.overpass.model.PostNotification;
import com.overpass.model.PostNotificationData;
import com.overpass.model.PushNotificationRequest;
import com.overpass.model.SmartLight;
import com.overpass.model.SmartLightResponse;
import com.overpass.model.User;
import com.overpass.reposiroty.DashboardRepository;
import com.overpass.reposiroty.MappingOverpassRepository;
import com.overpass.reposiroty.OverpassRepository;
import com.overpass.reposiroty.UserRepository;

import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private DashboardRepository dashboardRepository;
	
	@Autowired
	private OverpassRepository overpassRepository;
	
	@Value("${overpassUrl}")
	private String urlOvepass;
	
	@Value("${lineNotifyUrl}")
	private String lineNotifyUrl;
	
	@Value("${firebase.server-key}")
	private String serverKey;
	
	@Value("${firebase.enpoint}")
	private String endpoint;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private FCMService fcmService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MappingOverpassRepository mappingOverpassRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Dashboard getDataDashBoard(Authentication authentication) {
		User u = userRepository.getUserByUsername(authentication.getName());
		Dashboard obj = new Dashboard();
		obj.setOverpassAll(dashboardRepository.countOverpassAll(u.getGroupId()));
		obj.setOverpassByZone(dashboardRepository.countOverpassByZone(u.getGroupId()));
		obj.setOverpassOffByMonth(dashboardRepository.getOverpassByMonth(StatusLight.OFF, u.getGroupId()));
		obj.setOverpassOnByMonth(dashboardRepository.getOverpassByMonth(StatusLight.ON, u.getGroupId()));
		obj.setOverpassOn(dashboardRepository.countOverpassAllByStatus(StatusLight.ON, u.getGroupId()));
		obj.setOverpassOff(dashboardRepository.countOverpassAllByStatus(StatusLight.OFF, u.getGroupId()));
		obj.setDonutChart(dashboardRepository.getDataDonutChart(u.getGroupId()));
		obj.setOverpassOnMax(dashboardRepository.getMaxOverpassByStatus(u.getGroupId(), StatusLight.ON));
		obj.setOverpassOffMax(dashboardRepository.getMaxOverpassByStatus(u.getGroupId(), StatusLight.OFF));
		int overall = (obj.getOverpassAll().containsKey("cnt") && obj.getOverpassAll().get("cnt") != null && obj.getOverpassAll().get("cnt") != "") ? Integer.parseInt(obj.getOverpassAll().get("cnt").toString()) : 0;
		obj.setOverpassOffAverage(obj.getOverpassOffMax() / overall *  100);
		return obj;
	}
	
	@Override
	public List<Integer> validateOverpass() {
		List<Integer> result = new ArrayList<>();
		try {
			
			List<Overpass> overpasses = overpassRepository.getOverpassesByStatus(Status.ACTIVE);
			Map<String, String> overpassLastStatus = overpassRepository.getLastStatusOverpassStatus();
			for(Overpass o : overpasses) {
				String url = urlOvepass.replace(":id", o.getId());
				ResponseEntity<String> rest;
				try {
					rest = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
				} catch(Exception ex) {
					continue;
				}
				if(rest.getStatusCode() == HttpStatus.OK) {
					String rs = rest.getBody();
					ObjectMapper mapper = new ObjectMapper();
					SmartLight light = null;
					try {
						light = mapper.readValue(rs, SmartLight.class);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					if(light != null && light.getStatus() == 200 && !light.getResponse().isEmpty()) {
						
						SmartLightResponse res = light.getResponse().get(0);
						OverpassStatus overpass = new OverpassStatus();
						overpass.setOverpassId(res.getIdOverpass());  
						Date parsedDate;
						Timestamp timestamp;
						try {
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						    parsedDate = dateFormat.parse(res.getTimestamp());
						    timestamp = new java.sql.Timestamp(parsedDate.getTime());
						    overpass.setEffectiveDate(timestamp);
						} catch (ParseException e) {
							overpass.setEffectiveDate(new Timestamp(System.currentTimeMillis()));
						}
						overpass.setWatt(res.getWatt());
						Double w = 0.0;
						if(StatusLight.ON.name().equals(res.getStatus().toUpperCase()) && res.getWatt() < (o.getLightBulbCnt() * o.getLightBulb().getWatt())) {
							w = o.getLightBulbCnt() * o.getLightBulb().getWatt();
							overpass.setStatus(StatusLight.WARNING);
						}else if(StatusLight.ON.name().equals(res.getStatus().toUpperCase())) {
							overpass.setStatus(StatusLight.ON);
						}else if(StatusLight.OFF.name().equals(res.getStatus().toUpperCase())) {
							overpass.setStatus(StatusLight.OFF);
						}
						Map<String, MessageToNotify> messageToNotifys = new HashMap<>();
						if(overpassLastStatus.isEmpty() || (overpassLastStatus.containsKey(res.getIdOverpass()) && !overpassLastStatus.get(res.getIdOverpass()).equals(overpass.getStatus().name()))) {
							overpass.setActive("Y");
							overpass.setDistrict(o.getDistrictName());
							overpass.setAmphur(o.getAmphurName());
							overpass.setProvince(o.getProvinceName());
							overpass.setLocation(o.getLocation());
							overpass.setLatitude(o.getLatitude());
							overpass.setLongtitude(o.getLongtitude());
							String mapUrl = "http://www.google.com/maps/place/" + o.getLatitude() + "," + o.getLongtitude();
							overpass.setMapUrl(mapUrl);
							overpass.setId("" + res.getId());
							
							MessageToNotify messageNotify = getMessageToNotify(overpass, o.getProvince(), w);
							messageNotify.setId(overpass.getId());
							overpassRepository.updateActiveOverpassStatus(overpass.getOverpassId());
							overpass.setTopic(messageNotify.getTopic());
							overpass.setLocationDisplay(messageNotify.getLocation());
							Integer seq = dashboardRepository.getSeqOverpassStatusByOverpassIdAndStatus(overpass.getId(), overpass.getStatus());
							seq = (seq == null) ? 1 : seq;
							overpass.setSeq(seq);
							overpassRepository.insertOverpassStatus(overpass);
							senNotificationToLine(o.getId(), messageNotify);
							sendNotifyToWeb(o.getId(), messageNotify);
							messageToNotifys.put(overpass.getOverpassId(), messageNotify);
							//o.setOverpassStatus(overpass.getStatus().name());
							//o.setSetpointWatt(res.getWatt());
							List<GroupOverpass> groups = mappingOverpassRepository.getGroupByOverpassId(o.getId());
							for(GroupOverpass group : groups) {
								result.add(group.getId());
							}
						}
						sendEmail(messageToNotifys);
					}
				}
			}
		}catch(Exception ex) {
			log.error(ex.getMessage());
		}
		return result;
	}

	private void senNotificationToLine(String overpassId, MessageToNotify messageNotify){
		try {
			List<GroupOverpass> list = mappingOverpassRepository.getGroupByOverpassId(overpassId);
			for(GroupOverpass item : list) {
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
				headers.set("Authorization", "Bearer " + item.getLineNotiToken());
				MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
				String str = "";
				str += messageNotify.getTopic() + "\n";
				if(!StringUtil.isNullOrEmpty(messageNotify.getLocation())) {
					str += "สถานที่: " + messageNotify.getLocation() + "\n";
				}
				if(!StringUtil.isNullOrEmpty(messageNotify.getNote())) {
					str += "Note: " + messageNotify.getNote() + "\n";
				}
				if(!StringUtil.isNullOrEmpty(messageNotify.getTimeToHang())) {
					str += "วันเวลาที่ได้รับแจ้ง: " + messageNotify.getTimeToHang() + "\n";
				}
				if(!StringUtil.isNullOrEmpty(messageNotify.getCoordinate())) {
					str += "พิกัด: " + messageNotify.getCoordinate();
				}
				map.add("message", str);
				HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
				restTemplate.exchange(lineNotifyUrl, HttpMethod.POST, request, String.class);
			}
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	private void sendNotifyToWeb(String overpassId, MessageToNotify messageNotify) {
		try {
			List<GroupOverpass> list = mappingOverpassRepository.getGroupByOverpassId(overpassId);
			for(GroupOverpass item : list) {
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				headers.add("Authorization", serverKey);
				
				PostNotification post = new PostNotification();
				post.setTo("/topics/overpass-" + item.getId());
				post.setData(messageNotify);
				HttpEntity<PostNotification> entity = new HttpEntity<>(post, headers);
				restTemplate.postForObject(endpoint, entity, String.class);
			}
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	private MessageToNotify getMessageToNotify(OverpassStatus overpass, int provinceId, Double watt) {
		MessageToNotify noti = new MessageToNotify();
		try {
			noti.setStatus(overpass.getStatus());
			if(StatusLight.OFF.equals(overpass.getStatus())){
				noti.setTopic("แจ้งเตือนหลอดไฟดับ ");
			}else if(StatusLight.WARNING.equals(overpass.getStatus())){
				noti.setTopic("แจ้งเตือนพลังงานไฟลดลง " + overpass.getWatt() + " < " + watt + " watt");
			}else if(StatusLight.ON.equals(overpass.getStatus())){
				noti.setTopic("แจ้งเตือนหลอดไฟเปิด ");
			}
			String location = "";
			if(provinceId == 1) {
				if(!StringUtil.isNullOrEmpty(overpass.getAmphur())) {
					location += overpass.getAmphur() + " ";
				}
				if(!StringUtil.isNullOrEmpty(overpass.getDistrict())) {
					location += "แขวง" + overpass.getDistrict() + " ";
				}
				if(!StringUtil.isNullOrEmpty(overpass.getProvince())) {
					location += overpass.getProvince();
				}
				
			}else {
				if(!StringUtil.isNullOrEmpty(overpass.getDistrict())) {
					location += "ต." + overpass.getDistrict() + " ";
				}
				if(!StringUtil.isNullOrEmpty(overpass.getAmphur())) {
					location += "อ." + overpass.getAmphur() + " ";
				}
				if(!StringUtil.isNullOrEmpty(overpass.getProvince())) {
					location += "จ." + overpass.getProvince();
				}
			}
			noti.setCoordinate(overpass.getMapUrl());
			noti.setLocation(location);
			noti.setNote(overpass.getLocation());
			noti.setTimeToHang(Utils.dateFormatToString(Utils.convertTimestampToDate(overpass.getEffectiveDate()), "dd/MM/yyyy HH:mm:ss"));
			
			return noti;
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public Dashboard getDataDashBoard(Integer groupId) {
		Dashboard obj = new Dashboard();
		obj.setOverpassAll(dashboardRepository.countOverpassAll(groupId));
		obj.setOverpassByZone(dashboardRepository.countOverpassByZone(groupId));
		obj.setOverpassOffByMonth(dashboardRepository.getOverpassByMonth(StatusLight.OFF, groupId));
		obj.setOverpassOnByMonth(dashboardRepository.getOverpassByMonth(StatusLight.ON, groupId));
		obj.setOverpassOn(dashboardRepository.countOverpassAllByStatus(StatusLight.ON, groupId));
		obj.setOverpassOff(dashboardRepository.countOverpassAllByStatus(StatusLight.OFF, groupId));
		obj.setDonutChart(dashboardRepository.getDataDonutChart(groupId));
		obj.setOverpassOnMax(dashboardRepository.getMaxOverpassByStatus(groupId, StatusLight.ON));
		obj.setOverpassOffMax(dashboardRepository.getMaxOverpassByStatus(groupId, StatusLight.OFF));
		int overall = (obj.getOverpassAll().containsKey("cnt") && obj.getOverpassAll().get("cnt") != null && obj.getOverpassAll().get("cnt") != "") ? Integer.parseInt(obj.getOverpassAll().get("cnt").toString()) : 0;
		obj.setOverpassOffAverage(overall * obj.getOverpassOffMax() / 100);
		return obj;
	}
	
	private void sendEmail(Map<String, MessageToNotify> obj) {
		try {
			for(Map.Entry<String, MessageToNotify> item : obj.entrySet()) {
				List<GroupOverpass> list = mappingOverpassRepository.getGroupByOverpassId(item.getKey());
				for(GroupOverpass group : list) {
					String subject = item.getValue().getTopic();
					String body = item.getValue().getTopic();
					body += "\n\n              ";
					body += "สถานที่ : " + item.getValue().getLocation();
					if(!StringUtil.isNullOrEmpty(item.getValue().getNote())) {
						body += "\n              ";
						body += "Note : " + item.getValue().getNote();
					}
					body += "\n              ";
					body += "วันเวลาที่ได้รับแจ้ง : " + item.getValue().getTimeToHang();
					body += "\n              ";
					body += "พิกัด : " + item.getValue().getCoordinate();
					body += "\n\n";
		 			emailService.sendSimpleMessage(group.getEmail(), subject, body);
				}
			}
			
		}catch(Exception ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}
}
