package com.overpass.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.overpass.model.LightBulb;
import com.overpass.reposiroty.LightBulbRepository;

@Service
public class LightBulbServiceImpl implements LightBulbService {

	@Autowired
	private LightBulbRepository lightBulbRepository;
	
	@Override
	public List<LightBulb> getLightBulbList() {
		return lightBulbRepository.getLightBulbList();
	}

	@Override
	public void saveLightBulb(LightBulb light) {
		lightBulbRepository.saveLightBulb(light);
	}

	@Override
	public void updateLightBulb(LightBulb light) {
		lightBulbRepository.updateLightBulb(light);
	}

	@Override
	public void deleteLightBulb(LightBulb light) {
		lightBulbRepository.deleteLightBulb(light);
		
	}

}
