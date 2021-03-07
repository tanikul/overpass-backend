package com.overpass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.overpass.model.Overpass;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.service.OverpassService;

@RestController
@RequestMapping("api/overpass")
public class OverpassController {

	@Autowired
	private OverpassService overpassService;
	
	@PostMapping("/searchOverpass")
	public ResponseDataTable<Overpass> searchOverpass(@RequestBody SearchDataTable<Overpass> data, Authentication authentication) throws Exception{
		return overpassService.searchOverpass(data);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/save")
	public void insert(@RequestBody Overpass data, Authentication authentication) throws Exception{
		try {
			overpassService.insertOverpass(data, authentication);	
		}catch(Exception ex) {
			throw ex;
		}
		
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/update")
	public void update(@RequestBody Overpass data, Authentication authentication) throws Exception{
		try {
			overpassService.updateOverpass(data, authentication);	
		}catch(Exception ex) {
			throw ex;
		}
		
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/delete")
	public void update(String id) throws Exception{
		try {
			overpassService.deleteOverpass(id);	
		}catch(Exception ex) {
			throw ex;
		}
		
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@GetMapping()
	public List<Overpass> getOverpassesByCondition(@RequestParam(value = "provinceId", required = false) Integer provinceId, 
			@RequestParam(value = "amphurId", required = false) Integer amphurId,
			@RequestParam(value = "districtId", required = false) Integer districtId) throws Exception{
		return overpassService.getOverpasses(provinceId, amphurId, districtId);
	}
}
