package com.overpass.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.overpass.model.GroupOverpass;
import com.overpass.model.MapGroupOverpass;
import com.overpass.model.MapGroupOverpassRequest;
import com.overpass.model.MapUserGroup;
import com.overpass.model.Overpass;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.SearchGroupOverpass;
import com.overpass.model.SearchGroupOverpassResponse;

public interface MappingOverpassService {

	public void insertMapGroupAndOverpass(MapGroupOverpassRequest data, Authentication authen);
	public void updateMapGroupAndOverpass(MapGroupOverpassRequest data, Authentication authen);
	public void insertGroupOverpass(GroupOverpass data, Authentication authen);
	public void insertMapUserAndGroup(MapUserGroup data, Authentication authen);
	public List<Overpass> getOverPassByUserId(int userId);
	public SearchGroupOverpassResponse getOverPassByGroupId(int groupId);
	public void deleteGroupOverpass(int id);
	public void deleteMapUserAndGroup(int id);
	public ResponseDataTable<SearchGroupOverpassResponse> searchMappingOverPass(SearchDataTable<GroupOverpass> data);
	public List<GroupOverpass> getMappingGroupOverPassAll();
	public void deleteMapGroupAndOverpass(int groupId);
}
