package com.overpass.reposiroty;

import java.util.List;
import java.util.Map;

import com.overpass.common.Constants.Master;
import com.overpass.model.MasterData;
import com.overpass.model.Province;
import com.overpass.model.Role;

public interface MasterDataRepository {

	public List<MasterData> getMasterData(Master type);
	public MasterData getMasterDataById(Master type, int id);
	public List<Role> getRoles();
	public List<MasterData> getProvince();
	public List<MasterData> getAmphurByProvinceId(int provinceId);
	public List<MasterData> getDistrictByAmphurId(int amphurId);
	public List<Map<String, Object>> getMappingAddress();
}
