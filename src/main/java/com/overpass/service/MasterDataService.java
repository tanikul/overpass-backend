package com.overpass.service;

import java.util.List;
import java.util.Map;

import com.overpass.common.Constants.Master;
import com.overpass.model.MasterData;
import com.overpass.model.Province;
import com.overpass.model.ResponseMasterData;

public interface MasterDataService {

	public List<ResponseMasterData> getMasterData(Master type, String locale);
	public ResponseMasterData getMasterDataById(Master type, Integer id, String locale);
	public List<ResponseMasterData> getProvince(String locale);
	public List<ResponseMasterData> getAmphurByProvinceId(String locale, int provinceId);
	public List<ResponseMasterData> getDistrictByAmphurId(String locale, int amphurId);
	public List<Province> getMappingAddress();
}
