package com.overpass.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.overpass.model.GroupOverpass;
import com.overpass.model.MapGroupOverpass;
import com.overpass.model.MapGroupOverpassRequest;
import com.overpass.model.MapUserGroup;
import com.overpass.model.Overpass;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.SearchGroupOverpass;
import com.overpass.model.SearchGroupOverpassResponse;
import com.overpass.model.User;
import com.overpass.reposiroty.MappingOverpassRepository;
import com.overpass.reposiroty.UserRepository;

@Service
public class MappingOverpassServiceImpl implements MappingOverpassService {

	@Autowired
	private MappingOverpassRepository mappingOverpassRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void insertMapGroupAndOverpass(MapGroupOverpassRequest data, Authentication authen) {
		
		User u = userRepository.getUserByUsername(authen.getName());
		GroupOverpass group = new GroupOverpass();
		group.setCreateBy(u.getId());
		group.setUpdateBy(u.getId());
		group.setGroupName(data.getGroupName());
		group.setEmail(data.getEmail());
		group.setLineNotiToken(data.getLineNotiToken());
		int id = mappingOverpassRepository.insertGroupOverpass(group);
		
		if(id > 0 && !data.getOverpasses().isEmpty()) {
			for(Overpass o : data.getOverpasses()) {
				MapGroupOverpass map = new MapGroupOverpass();
				map.setGroupId(id);
				map.setOverpassId(o.getId());
				map.setCreateBy(u.getId());
				map.setUpdateBy(u.getId());
				mappingOverpassRepository.insertMapGroupAndOverpass(map);
			}
		}
	}
	
	@Override
	public void updateMapGroupAndOverpass(MapGroupOverpassRequest data, Authentication authen) {
		
		User u = userRepository.getUserByUsername(authen.getName());
		GroupOverpass group = new GroupOverpass();
		group.setId(data.getGroupId());
		group.setGroupName(data.getGroupName());
		group.setEmail(data.getEmail());
		group.setLineNotiToken(data.getLineNotiToken());
		group.setUpdateBy(u.getId());
		mappingOverpassRepository.updateGroupOverpass(group);
		mappingOverpassRepository.deleteMapGroupAndOverpassByGroupId(data.getGroupId());
		
		if(!data.getOverpasses().isEmpty()) {
			for(Overpass o : data.getOverpasses()) {
				MapGroupOverpass map = new MapGroupOverpass();
				map.setGroupId(data.getGroupId());
				map.setOverpassId(o.getId());
				map.setCreateBy(u.getId());
				map.setUpdateBy(u.getId());
				mappingOverpassRepository.insertMapGroupAndOverpass(map);
			}
		}
	}

	@Override
	public void insertGroupOverpass(GroupOverpass data, Authentication authen) {
		User u = userRepository.getUserByUsername(authen.getName());
		data.setCreateBy(u.getId());
		data.setUpdateBy(u.getId());
		mappingOverpassRepository.insertGroupOverpass(data);
		
	}

	@Override
	public void insertMapUserAndGroup(MapUserGroup data, Authentication authen) {
		User u = userRepository.getUserByUsername(authen.getName());
		data.setCreateBy(u.getId());
		data.setUpdateBy(u.getId());
		mappingOverpassRepository.insertMapUserAndGroup(data);
		
	}

	@Override
	public List<Overpass> getOverPassByUserId(int userId) {
		return mappingOverpassRepository.getOverPassByUserId(userId);
	}

	@Override
	public void deleteGroupOverpass(int id) {
		mappingOverpassRepository.deleteGroupOverpass(id);
		
	}

	@Override
	public void deleteMapUserAndGroup(int id) {
		mappingOverpassRepository.deleteMapUserAndGroup(id);
		
	}

	@Override
	public SearchGroupOverpassResponse getOverPassByGroupId(int id) {
		List<SearchGroupOverpass> rs = mappingOverpassRepository.getOverPassByGroupId(id);
		SearchGroupOverpassResponse obj = new SearchGroupOverpassResponse();
		List<Overpass> arrOverpass = new ArrayList<>();
		int groupId = 0;
		String groupName = "";
		String lineNotify = "";
		String email = "";
		for(SearchGroupOverpass item : rs) {
			Overpass o = new Overpass();
			o.setId(item.getOverpassId());
			o.setName(item.getOverpassName());
			o.setProvinceName(item.getProvinceName());
			o.setAmphurName(item.getAmphurName());
			o.setDistrictName(item.getDistrictName());
			o.setLocation(item.getLocation());
			arrOverpass.add(o);
			groupId = item.getGroupId();
			groupName = item.getGroupName();
			lineNotify = item.getLineNotifyToken();
			email = item.getEmail();
		}
		if(groupId > 0) {
			
			obj.setGroupId(groupId);
			obj.setGroupName(groupName);
			obj.setOverpasses(arrOverpass);
			obj.setLineNotifyToken(lineNotify);
			obj.setEmail(email);
		}
		return obj;
	}

	@Override
	public ResponseDataTable<SearchGroupOverpassResponse> searchMappingOverPass(SearchDataTable<GroupOverpass> data) {
		List<SearchGroupOverpass> rs = mappingOverpassRepository.searchMappingOverPass(data);
		List<SearchGroupOverpassResponse> arrGroup = new ArrayList<>();
		List<Overpass> arrOverpass = new ArrayList<>();
		ResponseDataTable<SearchGroupOverpassResponse> result = new ResponseDataTable<>();
		int groupId = 0;
		String groupName = "";
		for(SearchGroupOverpass item : rs) {
			if(groupId > 0 && groupId != item.getGroupId()) {
				SearchGroupOverpassResponse obj = new SearchGroupOverpassResponse();
				obj.setGroupId(groupId);
				obj.setGroupName(groupName);
				obj.setOverpasses(arrOverpass);
				arrGroup.add(obj);
				arrOverpass = new ArrayList<>();
			}
			Overpass o = new Overpass();
			o.setId(item.getOverpassId());
			o.setName(item.getOverpassName());
			o.setProvinceName(item.getProvinceName());
			o.setAmphurName(item.getAmphurName());
			o.setDistrictName(item.getDistrictName());
			o.setLocation(item.getLocation());
			arrOverpass.add(o);
			groupId = item.getGroupId();
			groupName = item.getGroupName();
		}
		if(groupId > 0) {
			SearchGroupOverpassResponse obj = new SearchGroupOverpassResponse();
			obj.setGroupId(groupId);
			obj.setGroupName(groupName);
			obj.setOverpasses(arrOverpass);
			arrGroup.add(obj);
		}
		result.setData(arrGroup);
		result.setTotalRecords(mappingOverpassRepository.count(data));
		return result;
	}

	@Override
	public List<GroupOverpass> getMappingGroupOverPassAll() {
		return mappingOverpassRepository.getMappingGroupOverPassAll();
	}

	@Override
	@Transactional
	public void deleteMapGroupAndOverpass(int groupId) {
		
		mappingOverpassRepository.deleteGroupOverpass(groupId);
		mappingOverpassRepository.deleteMapGroupAndOverpassByGroupId(groupId);
		
	}

}
