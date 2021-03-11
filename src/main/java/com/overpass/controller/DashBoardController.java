package com.overpass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overpass.model.Dashboard;
import com.overpass.model.OverpassStatus;
import com.overpass.model.SmartLight;
import com.overpass.model.SmartLightResponse;
import com.overpass.service.DashboardService;

@RestController
@RequestMapping("api/dashboard")
public class DashBoardController {

	@Autowired
	private DashboardService dashboardService;
	
	@GetMapping("/getDataOverpass")
	@ResponseBody
	public Dashboard insertMapGroupAndOverpass(){
		return dashboardService.getDataDashBoard();
	}
	
	@GetMapping("/test")
	@ResponseBody
	public Dashboard test() throws JsonMappingException, JsonProcessingException{
		return dashboardService.getDataDashBoard();
	}
}
