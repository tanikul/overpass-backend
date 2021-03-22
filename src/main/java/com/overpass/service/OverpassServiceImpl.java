package com.overpass.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.overpass.common.Constants.Status;
import com.overpass.common.Utils;
import com.overpass.model.LightBulb;
import com.overpass.model.Overpass;
import com.overpass.model.OverpassStatus;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;
import com.overpass.reposiroty.OverpassRepository;
import com.overpass.reposiroty.UserRepository;

@Service
public class OverpassServiceImpl implements OverpassService {

	@Autowired
	private OverpassRepository overpassRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void insertOverpass(Overpass overpass, Authentication authentication) {
		User u = userRepository.getUserByUsername(authentication.getName());
		overpass.setCreateBy(u.getId());
		overpassRepository.insertOverpass(overpass);
	}

	@Override
	public ResponseDataTable<Overpass> searchOverpass(SearchDataTable<Overpass> data) {
		return overpassRepository.searchOverpass(data);
	}

	@Override
	public void updateOverpass(Overpass overpass, Authentication authentication) {
		User u = userRepository.getUserByUsername(authentication.getName());
		overpass.setCreateBy(u.getId());
		overpassRepository.updateOverpass(overpass);
	}

	@Override
	public void deleteOverpass(String id) {
		overpassRepository.deleteOverpass(id);
	}

	@Override
	public List<Overpass> getOverpasses(Integer provinceId, Integer amphurId, Integer tumbon) {
		return overpassRepository.getOverpasses(provinceId, amphurId, tumbon);
	}

	@Override
	public List<Overpass> searchOverpassesByUserId(Integer provinceId, Integer amphurId, Integer tumbonId, String userId, String overpassId) {
		User u = userRepository.getUserByUsername(userId);
		return overpassRepository.searchOverpassesByUserId(provinceId, amphurId, tumbonId, u.getId(), overpassId);
	}

	@Override
	public List<LightBulb> getLightBulbAll() {
		return overpassRepository.getLightBulbAll();
	}

	@Override
	public List<OverpassStatus> getOverpassStatusByGroupId(String id) {
		return overpassRepository.getOverpassStatusByGroupId(id);
	}

}
