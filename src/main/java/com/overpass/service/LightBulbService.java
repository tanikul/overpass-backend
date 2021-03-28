package com.overpass.service;

import java.util.List;

import com.overpass.model.LightBulb;

public interface LightBulbService {

	public List<LightBulb> getLightBulbList();
	public void saveLightBulb(LightBulb light);
	public void updateLightBulb(LightBulb light);
	public void deleteLightBulb(LightBulb light);
}
