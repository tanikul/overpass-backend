package com.overpass.reposiroty;

import java.util.List;

import com.overpass.model.GroupOverpass;
import com.overpass.model.MapGroupOverpass;
import com.overpass.model.MapUserGroup;
import com.overpass.model.Overpass;
import com.overpass.model.SearchDataTable;
import com.overpass.model.SearchGroupOverpass;

public interface MappingOverpassRepository {

	public void insertMapGroupAndOverpass(MapGroupOverpass data);
	public int insertGroupOverpass(GroupOverpass data);
	public void insertMapUserAndGroup(MapUserGroup data);
	public List<Overpass> getOverPassByUserId(int userId);
	public List<SearchGroupOverpass> getOverPassByGroupId(int groupId);
	public List<SearchGroupOverpass> searchMappingOverPass(SearchDataTable<GroupOverpass> data);
	public boolean deleteMapGroupAndOverpassByGroupId(int id);
	public void deleteGroupOverpass(int id);
	public void deleteMapUserAndGroup(int id);
	public int count(SearchDataTable<GroupOverpass> data);
	public List<GroupOverpass> getMappingGroupOverPassAll();
	public List<GroupOverpass> getGroupByOverpassId(String overpassId);
}
