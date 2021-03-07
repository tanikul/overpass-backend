package com.overpass.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import static com.overpass.common.Constants.Status;
import static com.overpass.common.Constants.TH;
import static com.overpass.common.Constants.EN;
import com.overpass.common.Constants.Master;
import com.overpass.model.Amphur;
import com.overpass.model.District;
import com.overpass.model.MasterData;
import com.overpass.model.Province;
import com.overpass.model.ResponseMasterData;
import com.overpass.model.Role;
import com.overpass.reposiroty.MasterDataRepository;

@Service
public class MasterDataServiceImpl implements MasterDataService {

	@Autowired
	private MasterDataRepository masterDataRepository;
	
	@Override
	public List<ResponseMasterData> getMasterData(Master type, String locale) {
		List<ResponseMasterData> result = new ArrayList<ResponseMasterData>();
		if(type.equals(Master.ROLE)) {
			List<Role> rs = masterDataRepository.getRoles();
			for(Role item : rs) {
				ResponseMasterData data = new ResponseMasterData();
				data.setKey(item.getName());
				data.setValue(item.getName());
				result.add(data);
			}
			return result;
		}else if(type.equals(Master.STATUS)) {
			for(Status item : Status.values()) {
				ResponseMasterData data = new ResponseMasterData();
				data.setKey(item.name());
				data.setValue(item.name());
				result.add(data);
			}
			return result;
		}
		List<MasterData> rs = masterDataRepository.getMasterData(type);
		for(MasterData item : rs) {
			ResponseMasterData data = new ResponseMasterData();
			data.setKey(item.getId());
			data.setValue((locale.equals(TH)) ? item.getNameTh() : item.getNameEn());
			result.add(data);
		}
		return result;
	}

	@Override
	public ResponseMasterData getMasterDataById(Master type, Integer id, String locale) {
		ResponseMasterData result = new ResponseMasterData();
		MasterData rs = masterDataRepository.getMasterDataById(type, id);
		if(rs != null) {
			result.setKey(rs.getId());
			result.setValue((locale.equals(TH)) ? rs.getNameTh() : rs.getNameEn());
		}
		return result;
	}

	@Override
	public List<ResponseMasterData> getProvince(String locale) {
		List<ResponseMasterData> result = new ArrayList<ResponseMasterData>();
		List<MasterData> rs = masterDataRepository.getProvince();
		for(MasterData item : rs) {
			ResponseMasterData data = new ResponseMasterData();
			data.setKey(item.getId());
			data.setValue((locale.equals(TH)) ? item.getNameTh() : item.getNameEn());
			result.add(data);
		}
		return result;
	}

	@Override
	public List<ResponseMasterData> getAmphurByProvinceId(String locale, int provinceId) {
		List<ResponseMasterData> result = new ArrayList<ResponseMasterData>();
		List<MasterData> rs = masterDataRepository.getAmphurByProvinceId(provinceId);
		for(MasterData item : rs) {
			ResponseMasterData data = new ResponseMasterData();
			data.setKey(item.getId());
			data.setValue(item.getNameTh());
			result.add(data);
		}
		return result;
	}

	@Override
	public List<ResponseMasterData> getDistrictByAmphurId(String locale, int amphurId) {
		List<ResponseMasterData> result = new ArrayList<ResponseMasterData>();
		List<MasterData> rs = masterDataRepository.getDistrictByAmphurId(amphurId);
		for(MasterData item : rs) {
			ResponseMasterData data = new ResponseMasterData();
			data.setKey(item.getId());
			data.setValue(item.getNameTh());
			result.add(data);
		}
		return result;
	}

	@Override
	@Cacheable("province")
	public List<Province> getMappingAddress() {
		List<Province> result = new ArrayList<>();
		try {
			List<Map<String, Object>> rs = masterDataRepository.getMappingAddress();
			Province p = new Province();
			Amphur a = new Amphur();
			District d = new District();
			int provinceId = 0;
			int amphurId = 0;
			List<Amphur> arrAmphur = new ArrayList<>();
			List<District> arrDistrict = new ArrayList<>();
			
			for(Map<String, Object> item : rs) {
				if(provinceId != 0 && provinceId != Integer.parseInt(item.get("province_id").toString())) {
					a.setDistrict(arrDistrict);
					arrAmphur.add(a);
					p.setAmphur(arrAmphur);
					result.add(p);
					p = new Province();
					a = new Amphur();
					d = new District();
					arrAmphur = new ArrayList<>();
					arrDistrict = new ArrayList<>();
				}
				if(amphurId != 0 && item.get("amphur_id") != null && amphurId != Integer.parseInt(item.get("amphur_id").toString()) && a.getKey() > 0) {
					amphurId = Integer.parseInt(item.get("amphur_id").toString());			
					a.setDistrict(arrDistrict);
					arrAmphur.add(a);
					a = new Amphur();
					arrDistrict = new ArrayList<>();
				}
				if(item.get("district_id") != null) {
					d = new District();
					d.setKey(Integer.parseInt(item.get("district_id").toString()));
					d.setValue(item.get("district_name").toString());
					arrDistrict.add(d);
				}
				provinceId = Integer.parseInt(item.get("province_id").toString());
				p = new Province();
				p.setKey(provinceId);
				p.setValue(item.get("province_name").toString());
				
				if(item.get("amphur_id") != null) {
					amphurId = Integer.parseInt(item.get("amphur_id").toString());
					a.setKey(amphurId);
					a.setValue(item.get("amphur_name").toString());
					a.setPostCode(item.get("postcode").toString());
				}
				
			}
			
			if(!arrAmphur.isEmpty()) {
				if(!arrDistrict.isEmpty()) a.setDistrict(arrDistrict);
				arrAmphur.add(a);
				p.setAmphur(arrAmphur);
				result.add(p);
			}
		}catch(Exception ex) {
			throw ex;
		}
		
		return result;
	}

}
