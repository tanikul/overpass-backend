package com.overpass.reposiroty;

import java.util.Map;

import com.overpass.common.Constants.StatusLight;

public interface DashboardRepository {

	public Map<String, Object> countOverpassByZone();
	public Map<String, Object> countOverpassAll();
	public Map<String, Object> getOverpassOnOff();
	public Map<String, Object> getOverpassByMonth(StatusLight status);
	public Map<String, Object> countOverpassAllByStatus(StatusLight status);
}
