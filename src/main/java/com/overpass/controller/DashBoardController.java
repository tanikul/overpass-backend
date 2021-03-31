package com.overpass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
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
import com.overpass.model.User;
import com.overpass.service.DashboardService;
import com.overpass.service.EmailService;

@RestController
@RequestMapping("/apis/dashboard")
public class DashBoardController {

	@Autowired
	private DashboardService dashboardService;
	@Autowired
	EmailService emailService;
	@GetMapping("/getDataOverpass")
	@ResponseBody
	public Dashboard insertMapGroupAndOverpass(Authentication authentication){
		return dashboardService.getDataDashBoard(authentication);
	}
	
	@GetMapping("/test")
	@ResponseBody
	public Dashboard test(Authentication authentication) throws JsonMappingException, JsonProcessingException{
		
		User user = new User();
		user.setUsername("xxx");
		user.setPassword("cccc");
		user.setFirstName("firstName");
		user.setLastName("vvvvv");
		user.setEmail("tanikul.sa@gmail.com");
		//senEmail(user);
		return dashboardService.getDataDashBoard(authentication);
	}
	
	private void senEmail(User user) {
		try {
			String subject = "แจ้งการลงทะเบียนกับระบบ Smart Light Bangkok";
			String body = "เรียนคุณ " + user.getFirstName() + " " + user.getLastName();
			body += "\n\n              ";
			body += "ระบบได้ทำการลงทะเบียนให้คุณแล้ว โดยใช้";
			body += "\n                ";
			body += "username : " + user.getUsername();
			body += "\n                ";
			body += "password : " + user.getPassword();
			body += "\n\n";
			//body += "ขอบคุณครับ";
 			emailService.sendSimpleMessage(user.getEmail(), subject, body);
		}catch(Exception ex) {
			//log.error(ex.getMessage());
			throw ex;
		}
	}
}
