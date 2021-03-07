package com.overpass.reposiroty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.overpass.common.Constants.Master;
import com.overpass.model.MasterData;
import com.overpass.model.Province;
import com.overpass.model.Role;

@Repository
public class MasterDataRepositoryImpl implements MasterDataRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	@Cacheable("master")
	public List<MasterData> getMasterData(Master type) {
		try {
			return jdbcTemplate.query("select * from master_data where type = ? ", new RowMapper<MasterData>(){

				@Override
				public MasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
					MasterData m = new MasterData();
					m.setId(rs.getInt("id"));
					m.setNameEn(rs.getString("name_en"));
					m.setNameTh(rs.getString("name_th"));
					return m;
				}
			},  new Object[]{ type.name() });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}

	@Override
	@Cacheable("master_by_id")
	public MasterData getMasterDataById(Master type, int id) {
		try {
			return jdbcTemplate.queryForObject("select * from master_data where type = ? and id = ?", new RowMapper<MasterData>(){

				@Override
				public MasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
					MasterData m = new MasterData();
					m.setId(rs.getInt("id"));
					m.setNameEn(rs.getString("name_en"));
					m.setNameTh(rs.getString("name_th"));
					return m;
				}
			}, new Object[]{ type.name(), id });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}

	@Override
	@Cacheable("role")
	public List<Role> getRoles() {
		try {
			return jdbcTemplate.query("select * from roles order by name asc ", new RowMapper<Role>(){

				@Override
				public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
					Role r = new Role();
					r.setName(rs.getString("name"));
					r.setDescription(rs.getString("description"));
					return r;
				}
			});
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}

	@Override
	public List<MasterData> getProvince() {
		try {
			return jdbcTemplate.query("select * from province order by province_id asc", new RowMapper<MasterData>(){

				@Override
				public MasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
					MasterData m = new MasterData();
					m.setId(rs.getInt("province_id"));
					m.setNameEn(rs.getString("province_name_eng"));
					m.setNameTh(rs.getString("province_name"));
					return m;
				}
			}, new Object[]{ });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}

	@Override
	public List<MasterData> getAmphurByProvinceId(int provinceId) {
		try {
			return jdbcTemplate.query("select * from amphur where province_id = ? order by amphur_id asc", new RowMapper<MasterData>(){
	
				@Override
				public MasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
					MasterData m = new MasterData();
					m.setId(rs.getInt("amphur_id"));
					m.setNameTh(rs.getString("amphur_name"));
					return m;
				}
			}, new Object[]{ provinceId });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}

	@Override
	public List<MasterData> getDistrictByAmphurId(int amphurId) {
		try {
			return jdbcTemplate.query("select * from district where amphur_id = ? order by district_id asc", new RowMapper<MasterData>(){
	
				@Override
				public MasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
					MasterData m = new MasterData();
					m.setId(rs.getInt("district_id"));
					m.setNameTh(rs.getString("district_name"));
					return m;
				}
			}, new Object[]{ amphurId });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}

	@Override
	public List<Map<String, Object>> getMappingAddress() {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select p.province_id, p.province_name, a.amphur_id, a.amphur_name, d.district_id, d.district_name, a.postcode from province p left join amphur a on p.province_id = a.province_id left join district d ");
			sql.append("on d.amphur_id = a.amphur_id and d.province_id = a.province_id ");
			sql.append("order by p.province_name, a.amphur_name, d.district_name asc");
			
			return jdbcTemplate.queryForList(sql.toString());	
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}
}
