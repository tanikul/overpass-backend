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

import com.overpass.model.GroupOverpass;
import com.overpass.model.MapGroupOverpass;
import com.overpass.model.MapGroupOverpassRequest;
import com.overpass.model.MapUserGroup;
import com.overpass.model.Overpass;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.SearchGroupOverpassResponse;
import com.overpass.service.MappingOverpassService;

@RestController
@RequestMapping("api/mapping")
public class MappingOverpassController {

	@Autowired
	private MappingOverpassService mappingOverpassService;
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/insertMapGroupAndOverpass")
	public void insertMapGroupAndOverpass(@RequestBody MapGroupOverpassRequest data, Authentication authentication){
		mappingOverpassService.insertMapGroupAndOverpass(data, authentication);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/updateMapGroupAndOverpass")
	public void updateMapGroupAndOverpass(@RequestBody MapGroupOverpassRequest data, Authentication authentication){
		mappingOverpassService.updateMapGroupAndOverpass(data, authentication);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/insertGroupOverpass")
	public void insertGroupOverpass(@RequestBody GroupOverpass data, Authentication authentication){
		mappingOverpassService.insertGroupOverpass(data, authentication);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/insertMapUserAndGroup")
	public void insertMapUserAndGroup(@RequestBody MapUserGroup data, Authentication authentication){
		mappingOverpassService.insertMapUserAndGroup(data, authentication);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/deleteMapGroupAndOverpass")
	public void deleteMapGroupAndOverpass(int id){
		mappingOverpassService.deleteMapGroupAndOverpass(id);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/deleteGroupOverpass")
	public void deleteGroupOverpass(int id){
		mappingOverpassService.deleteGroupOverpass(id);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/deleteMapUserAndGroup")
	public void deleteMapUserAndGroup(int id){
		mappingOverpassService.deleteMapUserAndGroup(id);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@GetMapping("/getOverPassByUserId")
	public List<Overpass> getOverPassByUserId(@RequestParam("id") int id) throws Exception{
		return mappingOverpassService.getOverPassByUserId(id);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@GetMapping("/getOverPassByGroupId")
	public SearchGroupOverpassResponse getOverPassByGroupId(@RequestParam("groupId") int groupId) throws Exception{
		return mappingOverpassService.getOverPassByGroupId(groupId);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/searchMappingOverPass")
	public ResponseDataTable<SearchGroupOverpassResponse> searchMappingOverPass(@RequestBody SearchDataTable<GroupOverpass> data){
		return mappingOverpassService.searchMappingOverPass(data);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@GetMapping("/getMappingOverPassAll")
	public List<GroupOverpass> getMappingGroupOverPassAll() {
		return mappingOverpassService.getMappingGroupOverPassAll();
	}
}
