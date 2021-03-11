package com.overpass.reposiroty;

import java.util.List;
import java.util.Map;

import com.overpass.common.Constants.Status;
import com.overpass.model.LightBulb;
import com.overpass.model.Overpass;
import com.overpass.model.OverpassStatus;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;

public interface OverpassRepository {

	public void insertOverpass(Overpass overpass);
	public void updateOverpass(Overpass overpass);
	public ResponseDataTable<Overpass> searchOverpass(SearchDataTable<Overpass> data);
	public int countById(String id);
	public void deleteOverpass(String id);
	public List<Overpass> getOverpasses(Integer provinceId, Integer amphurId, Integer tumbon);
	public void insertOverpassStatus(OverpassStatus overpass);
	public void updateActiveOverpassStatus(String overpassId);
	public List<Overpass> getOverpassesByStatus(Status status);
	public Map<String, String> getLastStatusOverpassStatus();
	public List<Overpass> searchOverpassesByUserId(Integer provinceId, Integer amphurId, Integer tumbon, int userId, String overpassId);
	public List<LightBulb> getLightBulbAll();
}
