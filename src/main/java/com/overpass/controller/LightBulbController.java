package com.overpass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.overpass.model.LightBulb;
import com.overpass.service.LightBulbService;

@RestController
@RequestMapping("api/lightbulb")
public class LightBulbController {

	@Autowired
	private LightBulbService lightBulbService;
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@GetMapping("/getLightBulbList")
	public List<LightBulb> getLightBulbList() {
		return lightBulbService.getLightBulbList();
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/save")
	public void insert(@RequestBody LightBulb light, Authentication authentication) throws Exception{
		try {
			lightBulbService.saveLightBulb(light);	
		}catch(Exception ex) {
			throw ex;
		}
		
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/update")
	public void updateLightBulb(@RequestBody LightBulb light, Authentication authentication) throws Exception{
		try {
			lightBulbService.updateLightBulb(light);	
		}catch(Exception ex) {
			throw ex;
		}
		
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/delete")
	public void deleteLightBulb(@RequestBody LightBulb light, Authentication authentication) throws Exception{
		try {
			lightBulbService.deleteLightBulb(light);	
		}catch(Exception ex) {
			throw ex;
		}
		
	}
}
