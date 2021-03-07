package com.overpass.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
	
	@Value("${url.overpass}")
	private String urlOvepass;
	
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
			Map<String, Object> overpassLastStatus = overpassRepository.getLastStatusOverpassStatus();
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
					if(light != null && light.getStatus() == 200) {
						
						SmartLightResponse res = light.getResponse().get(0);
						OverpassStatus overpass = new OverpassStatus();
						overpass.setOverpassId(res.getIdOverpass());
						overpass.setEffectiveDate(res.getTimestamp());
						overpass.setWatt(res.getWatt());
						if(StatusLight.ON.name().equals(res.getStatus().toUpperCase()) && res.getWatt() < o.getSetpointWatt()) {
							overpass.setStatus(StatusLight.WARNING);
						}else if(StatusLight.ON.name().equals(res.getStatus().toUpperCase())) {
							overpass.setStatus(StatusLight.ON);
						}else if(StatusLight.OFF.name().equals(res.getStatus().toUpperCase())) {
							overpass.setStatus(StatusLight.OFF);
						}
						if(overpassLastStatus == null || !overpassLastStatus.get(res.getIdOverpass()).equals(overpass.getStatus())) {
							overpass.setActive("Y");
							overpassRepository.updateActiveOverpassStatus(overpass.getOverpassId());
							overpassRepository.insertOverpassStatus(overpass);
							chk = true;
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

}
