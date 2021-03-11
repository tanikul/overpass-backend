package com.overpass.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overpass.common.Constants.Status;
import com.overpass.common.Constants.StatusLight;
import com.overpass.model.Dashboard;
import com.overpass.model.Overpass;
import com.overpass.model.OverpassStatus;
import com.overpass.model.SmartLight;
import com.overpass.model.SmartLightResponse;
import com.overpass.reposiroty.DashboardRepository;
import com.overpass.reposiroty.OverpassRepository;

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
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public Dashboard getDataDashBoard() {
		Dashboard obj = new Dashboard();
		obj.setOverpassAll(dashboardRepository.countOverpassAll());
		obj.setOverpassByZone(dashboardRepository.countOverpassByZone());
		obj.setOverpassOffByMonth(dashboardRepository.getOverpassByMonth(StatusLight.OFF));
		obj.setOverpassOnByMonth(dashboardRepository.getOverpassByMonth(StatusLight.ON));
		obj.setOverpassOn(dashboardRepository.countOverpassAllByStatus(StatusLight.ON));
		obj.setOverpassOff(dashboardRepository.countOverpassAllByStatus(StatusLight.OFF));
		return obj;
	}
	
	@Override
	public boolean validateOverpass() {
		try {
			boolean chk = false;
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
						overpass.setEffectiveDate(res.getTimestamp());
						overpass.setWatt(res.getWatt());
						if(StatusLight.ON.name().equals(res.getStatus().toUpperCase()) && res.getWatt() < (o.getLightBulbCnt() * o.getLightBulb().getWatt())) {
							overpass.setStatus(StatusLight.WARNING);
						}else if(StatusLight.ON.name().equals(res.getStatus().toUpperCase())) {
							overpass.setStatus(StatusLight.ON);
						}else if(StatusLight.OFF.name().equals(res.getStatus().toUpperCase())) {
							overpass.setStatus(StatusLight.OFF);
						}
						if(overpassLastStatus.isEmpty() || (overpassLastStatus.containsKey(res.getIdOverpass()) && !overpassLastStatus.get(res.getIdOverpass()).equals(overpass.getStatus().name()))) {
							overpass.setActive("Y");
							overpassRepository.updateActiveOverpassStatus(overpass.getOverpassId());
							overpassRepository.insertOverpassStatus(overpass);
							chk = true;
							senNotificationToLine(o, overpass);
						}
					}
				}
			}
			return chk;
		}catch(Exception ex) {
			log.error(ex.getMessage());
		}
		return false;
	}

	private void senNotificationToLine(Overpass overpass, OverpassStatus status) {
		if(StatusLight.OFF.equals(status.getStatus()) || StatusLight.WARNING.equals(status.getStatus())) {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
			headers.set("Authorization", "Bearer " + overpass.getLineNotiToken());
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("message", status.getStatus().name());
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			restTemplate.exchange(lineNotifyUrl, HttpMethod.POST, request, String.class);
		}
		
		
	}

}
