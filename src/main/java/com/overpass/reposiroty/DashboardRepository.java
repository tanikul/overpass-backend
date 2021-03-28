package com.overpass.reposiroty;

import java.util.List;
import java.util.Map;

import com.overpass.common.Constants.StatusLight;

public interface DashboardRepository {

	public Map<String, Object> countOverpassByZone(int groupId);
	public Map<String, Object> countOverpassAll(int groupId);
	public Map<String, Object> getOverpassOnOff(int groupId);
	public Map<String, Object> getOverpassByMonth(StatusLight status, int groupId);
	public Map<String, Object> countOverpassAllByStatus(StatusLight status, int groupId);
	public List<Map<String, Object>> getDataDonutChart(int groupId);
	public Integer getSeqOverpassStatusByOverpassIdAndStatus(String overpassId, StatusLight status);
	public Integer getMaxOverpassByStatus(int groupId, StatusLight status);
}
