package com.overpass.reposiroty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.overpass.common.Constants.Status;
import com.overpass.common.Constants.StatusLight;
import com.overpass.model.LightBulb;
import com.overpass.model.Overpass;
import com.overpass.model.OverpassStatus;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;

@Repository
public class OverpassRepositoryImpl implements OverpassRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void insertOverpass(Overpass overpass) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into overpass (id, name, latitude, longtitude, location, district, amphur, province, postcode, create_dt, update_dt, create_by, update_by, status, light_bulb_id, light_bulb_cnt)");
			sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?, ?, ?, ? )");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setString(1, overpass.getId());
					ps.setString(2, overpass.getName());
					ps.setString(3, overpass.getLatitude());
					ps.setString(4, overpass.getLongtitude());
					ps.setString(5, overpass.getLocation());
					ps.setInt(6, overpass.getDistrict());
					ps.setInt(7, overpass.getAmphur());
					ps.setInt(8, overpass.getProvince());
					ps.setString(9, overpass.getPostcode());
					ps.setInt(10, overpass.getCreateBy());
					ps.setInt(11, overpass.getUpdateBy());
					ps.setString(12, overpass.getStatus().name());
					ps.setInt(13, overpass.getLightBulbId());
					ps.setInt(14, overpass.getLightBulbCnt());
					return ps.execute();
				}  
			});  
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public ResponseDataTable<Overpass> searchOverpass(SearchDataTable<Overpass> data) {
		StringBuilder sql = new StringBuilder();
		List<String> wheres = new ArrayList<>();
		List<Overpass> result = new ArrayList<>();
		ResponseDataTable<Overpass> response = new ResponseDataTable<>();
		
		try {
			sql.append("select o.id, o.name, o.location, o.province, o.amphur, o.district, d.district_name, a.amphur_name, p.province_name, o.light_bulb_id, o.light_bulb_cnt, o.setpoint_watt, a.postcode, latitude, longtitude, o.status from overpass o inner join province p on o.province = p.province_id left join amphur a on a.amphur_id = o.amphur left join district d on d.district_id = o.district ");
			
			if(data.getFilter() != null) {
				if(StringUtils.isNotBlank(data.getFilter().getId())) {
					wheres.add("o.id like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getName())) {
					wheres.add("o.name like ?");
				}
				if(data.getFilter().getDistrict() > 0) {
					wheres.add("o.district = ?");
				}
				if(data.getFilter().getAmphur() > 0) {
					wheres.add("o.amphur = ?");
				}
				if(data.getFilter().getProvince() > 0) {
					wheres.add("o.province like ?");
				}
				if(data.getFilter().getStatus() != null) {
					wheres.add("o.status like ?");
				}
			}
			if(!wheres.isEmpty()) {
				sql.append(" where ");
				for(String where : wheres){
					sql.append(where).append(" and ");
				}
				sql = new StringBuilder(sql.substring(0, sql.length() - 4));
			}
			
			sql.append(" order by ");
			sql.append((StringUtils.isNotBlank(data.getSort())) ? getFieldOrderBy(data.getSort()) : "o.id");
			sql.append(" ");
			sql.append((StringUtils.isNotBlank(data.getOrder())) ? data.getOrder() : "asc");
			sql.append(" limit " + data.getLimit() + " offset " + (data.getLimit() * data.getPage()));
			
			result = jdbcTemplate.query(sql.toString(), 
					new PreparedStatementSetter() {
				   
		         public void setValues(PreparedStatement preparedStatement) throws SQLException {
		        	Overpass u = data.getFilter();
		        	int i = 1;
		        	
		        	if(StringUtils.isNotBlank(data.getFilter().getId())) {
		 				preparedStatement.setString(i++, "%" + u.getId() + "%");
		 			}
		 			if(StringUtils.isNotBlank(data.getFilter().getName())) {
		 				preparedStatement.setString(i++, "%" + u.getName() + "%");
		 			}
		 			if(data.getFilter().getDistrict() > 0) {
		 				preparedStatement.setInt(i++, u.getDistrict());
		 			}
		 			if(data.getFilter().getAmphur() > 0) {
		 				preparedStatement.setInt(i++, u.getAmphur());
		 			}
		 			if(data.getFilter().getProvince() > 0) {
		 				preparedStatement.setInt(i++, u.getProvince());
		 			}
		 			if(data.getFilter().getStatus() != null) {
		 				preparedStatement.setString(i++, u.getStatus().name());
		 			}
		         }
			},
		         new RowMapper<Overpass>(){
				
					@Override
					public Overpass mapRow(ResultSet rs, int rowNum) throws SQLException {
						Overpass o = new Overpass();
						o.setId(rs.getString("id"));
						o.setName(rs.getString("name"));
						o.setLocation(rs.getString("location"));
						o.setDistrictName(rs.getString("district_name"));
						o.setAmphurName(rs.getString("amphur_name"));
						o.setProvinceName(rs.getString("province_name"));
						o.setLatitude(rs.getString("latitude"));
						o.setLongtitude(rs.getString("longtitude"));
						o.setSetpointWatt(rs.getDouble("setpoint_watt"));
						o.setPostcode(rs.getString("postcode"));
						o.setProvince(rs.getInt("province"));
						o.setAmphur(rs.getInt("amphur"));
						o.setDistrict(rs.getInt("district"));
						o.setStatus(Status.valueOf(rs.getString("status")));
						o.setLightBulbId(rs.getInt("light_bulb_id"));
						o.setLightBulbCnt(rs.getInt("light_bulb_cnt"));
						return o;
					}
				}
		    );
			response.setData(result);
			response.setTotalRecords(count(data));
		}catch(Exception ex) {
			throw ex;
		}
		return response;
	}

	@Override
	public int countById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private int count(SearchDataTable<Overpass> data) {
		StringBuilder sql = new StringBuilder();
		List<String> wheres = new ArrayList<>();
		
		try {
			sql.append("select count(0) from overpass o ");
			
			if(data.getFilter() != null) {
				if(StringUtils.isNotBlank(data.getFilter().getId())) {
					wheres.add("o.id like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getName())) {
					wheres.add("o.name like ?");
				}
				if(data.getFilter().getDistrict() > 0) {
					wheres.add("o.district = ?");
				}
				if(data.getFilter().getAmphur() > 0) {
					wheres.add("o.amphur = ?");
				}
				if(data.getFilter().getProvince() > 0) {
					wheres.add("o.province like ?");
				}
				if(data.getFilter().getStatus() != null) {
					wheres.add("o.status like ?");
				}
			}
			if(!wheres.isEmpty()) {
				sql.append(" where ");
				for(String where : wheres){
					sql.append(where).append(" and ");
				}
				sql = new StringBuilder(sql.substring(0, sql.length() - 4));
			}
			List<Object> objs = new ArrayList<Object>();
			Overpass o = data.getFilter();
			
			if(StringUtils.isNotBlank(data.getFilter().getId())) {
				objs.add("%" + o.getId() + "%");
			}
			if(StringUtils.isNotBlank(data.getFilter().getName())) {
				objs.add("%" + o.getName() + "%");
			}
			if(data.getFilter().getDistrict() > 0) {
				objs.add(o.getDistrict());
			}
			if(data.getFilter().getAmphur() > 0) {
				objs.add(o.getAmphur());
			}
			if(data.getFilter().getProvince() > 0) {
				objs.add(o.getProvince());
			}
			if(data.getFilter().getStatus() != null) {
				objs.add(o.getStatus());
			}
			return jdbcTemplate.queryForObject(sql.toString(), Integer.class, objs.toArray());
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public void updateOverpass(Overpass overpass) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("update overpass set name = ?, latitude = ?, longtitude = ?, location = ?, district = ?, amphur = ?, province = ?, postcode = ?, update_dt = NOW(), update_by = ?, status = ?, light_bulb_id = ?, light_bulb_cnt = ? where id = ?");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
	
					ps.setString(1, overpass.getName());
					ps.setString(2, overpass.getLatitude());
					ps.setString(3, overpass.getLongtitude());
					ps.setString(4, overpass.getLocation());
					ps.setInt(5, overpass.getDistrict());
					ps.setInt(6, overpass.getAmphur());
					ps.setInt(7, overpass.getProvince());
					ps.setString(8, overpass.getPostcode());
					ps.setInt(9, overpass.getUpdateBy());
					ps.setString(10, overpass.getStatus().name());
					ps.setInt(11, overpass.getLightBulbId());
					ps.setInt(12, overpass.getLightBulbCnt());
					ps.setString(13, overpass.getId());
					return ps.execute();
				}  
			});
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public void deleteOverpass(String id) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from overpass where id = ?");
		jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
		 

			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setString(1, id);
				return ps.execute();
			}  
		});  
	}
	
	private String getFieldOrderBy(String column) {
		
		Map<String, String> map = new HashMap<>();
		map.put("districtName", "d.district_name");
		map.put("lastName", "last_name");
		map.put("amphurName", "a.amphur_name");
		map.put("provinceName", "p.province_name");
		map.put("status", "o.status");
		if(map.containsKey(column)) return map.get(column);
		return column;
	}

	@Override
	public List<Overpass> getOverpasses(Integer provinceId, Integer amphurId, Integer tumbonId) {
		try {
			List<String> wheres = new ArrayList<>();
			
			List<Object> objs = new ArrayList<Object>();
			StringBuilder sql = new StringBuilder();
			
			sql.append("select o.id, o.name, o.location, o.province, o.amphur, o.district, d.district_name, a.amphur_name, p.province_name, o.setpoint_watt, a.postcode, latitude, longtitude, o.status from overpass o inner join province p on o.province = p.province_id left join amphur a on a.amphur_id = o.amphur left join district d on d.district_id = o.district ");
			if(provinceId != null && provinceId > 0) {
				wheres.add("o.province = ?");
				objs.add(provinceId);
			}
			if(amphurId != null && amphurId > 0) {
				wheres.add("o.amphur = ?");
				objs.add(amphurId);
			}
			if(tumbonId != null && tumbonId > 0) {
				wheres.add("o.district = ?");
				objs.add(tumbonId);
			}
			if(!wheres.isEmpty()) {
				sql.append(" where ");
				for(String where : wheres){
					sql.append(where).append(" and ");
				}
				sql = new StringBuilder(sql.substring(0, sql.length() - 4));
			}
			
			return jdbcTemplate.query(sql.toString(), new RowMapper<Overpass>(){
	
				@Override
				public Overpass mapRow(ResultSet rs, int rowNum) throws SQLException {
					Overpass o = new Overpass();
					o.setId(rs.getString("id"));
					o.setName(rs.getString("name"));
					o.setLocation(rs.getString("location"));
					o.setDistrictName(rs.getString("district_name"));
					o.setAmphurName(rs.getString("amphur_name"));
					o.setProvinceName(rs.getString("province_name"));
					o.setLatitude(rs.getString("latitude"));
					o.setLongtitude(rs.getString("longtitude"));
					o.setSetpointWatt(rs.getDouble("setpoint_watt"));
					o.setPostcode(rs.getString("postcode"));
					o.setProvince(rs.getInt("province"));
					o.setAmphur(rs.getInt("amphur"));
					o.setDistrict(rs.getInt("district"));
					o.setStatus(Status.valueOf(rs.getString("status")));
					return o;
				}
			}, objs.toArray());
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}

	@Override
	public void insertOverpassStatus(OverpassStatus overpass) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into overpass_status (overpass_id, watt, status, effective_date, active, id, location, district, amphur, province, latitude, longtitude, map_url, topic, location_display)");
			sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setString(1, overpass.getOverpassId());
					ps.setDouble(2, overpass.getWatt());
					ps.setString(3, overpass.getStatus().name());
					ps.setTimestamp(4, overpass.getEffectiveDate());
					ps.setString(5, overpass.getActive());
					ps.setString(6, overpass.getId());
					ps.setString(7, overpass.getLocation());
					ps.setString(8, overpass.getDistrict());
					ps.setString(9, overpass.getAmphur());
					ps.setString(10, overpass.getProvince());
					ps.setString(11, overpass.getLatitude());
					ps.setString(12, overpass.getLongtitude());
					ps.setString(13, overpass.getMapUrl());
					ps.setString(14, overpass.getTopic());
					ps.setString(15, overpass.getLocationDisplay());
					return ps.execute();
				}  
			});  
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public List<Overpass> getOverpassesByStatus(Status status) {
		try {
			List<Object> objs = new ArrayList<Object>();
			objs.add(status.name());
			StringBuilder sql = new StringBuilder();
			sql.append("select o.id, o.name, o.location, o.province, o.amphur, o.district, d.district_name, a.amphur_name, p.province_name, o.setpoint_watt, a.postcode, latitude, longtitude, o.status, o.light_bulb_id, o.light_bulb_cnt, l.watt from overpass o inner join light_bulb l on o.light_bulb_id = l.id inner join province p on o.province = p.province_id left join amphur a on a.amphur_id = o.amphur left join district d on d.district_id = o.district where o.status = ? ");
			
			return jdbcTemplate.query(sql.toString(), new RowMapper<Overpass>(){
	
				@Override
				public Overpass mapRow(ResultSet rs, int rowNum) throws SQLException {
					Overpass o = new Overpass();
					o.setId(rs.getString("id"));
					o.setName(rs.getString("name"));
					o.setLocation(rs.getString("location"));
					o.setDistrictName(rs.getString("district_name"));
					o.setAmphurName(rs.getString("amphur_name"));
					o.setProvinceName(rs.getString("province_name"));
					o.setLatitude(rs.getString("latitude"));
					o.setLongtitude(rs.getString("longtitude"));
					o.setSetpointWatt(rs.getDouble("setpoint_watt"));
					o.setPostcode(rs.getString("postcode"));
					o.setProvince(rs.getInt("province"));
					o.setAmphur(rs.getInt("amphur"));
					o.setDistrict(rs.getInt("district"));
					o.setStatus(Status.valueOf(rs.getString("status")));
					o.setLightBulbCnt(rs.getInt("light_bulb_cnt"));
					o.setLightBulbId(rs.getInt("light_bulb_id"));
					LightBulb light = new LightBulb();
					light.setWatt(rs.getDouble("watt"));
					light.setId(rs.getInt("light_bulb_id"));
					o.setLightBulb(light);
					return o;
				}
			}, objs.toArray());
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}
	
	@Override
	public Map<String, String> getLastStatusOverpassStatus() {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select overpass_id, status from overpass_status where active = 'Y'");
			
			return jdbcTemplate.query(sql.toString(), (ResultSet rs) -> {
				Map<String, String> result = new HashMap<>();
				while (rs.next()) {
                   result.put(rs.getString("overpass_id"), rs.getString("status"));
                }
                return result;
			},new Object[] { });
		}catch(EmptyResultDataAccessException ex) {
			return null;
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public void updateActiveOverpassStatus(String overpassId) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("update overpass_status set active = 'N' where overpass_id = ?");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

					ps.setString(1, overpassId);
					return ps.execute();
				}  
			});
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public List<Overpass> searchOverpassesByUserId(Integer provinceId, Integer amphurId, Integer tumbonId, int userId, String overpassId) {
		try {
			List<String> wheres = new ArrayList<>();
			
			List<Object> objs = new ArrayList<Object>();
			StringBuilder sql = new StringBuilder();
			
			sql.append("select o.id, o.name, os.status overpass_status, o.location, o.province, o.amphur, o.district, d.district_name, a.amphur_name, p.province_name, o.setpoint_watt, a.postcode, o.latitude, o.longtitude, o.status ");
			sql.append(" from users u inner join map_group_overpass m on u.group_id = m.group_id inner join overpass o on o.id = m.overpass_id and o.status = 'ACTIVE' ");
			sql.append(" left join overpass_status os on o.id = os.overpass_id and active = 'Y'");
			sql.append(" inner join province p on o.province = p.province_id left join amphur a on a.amphur_id = o.amphur left join district d on d.district_id = o.district ");
			if(provinceId != null && provinceId > 0) {
				wheres.add("o.province = ?");
				objs.add(provinceId);
			}
			if(amphurId != null && amphurId > 0) {
				wheres.add("o.amphur = ?");
				objs.add(amphurId);
			}
			if(tumbonId != null && tumbonId > 0) {
				wheres.add("o.district = ?");
				objs.add(tumbonId);
			}
			if(!StringUtils.isAllBlank(overpassId)) {
				wheres.add("o.id = ?");
				objs.add(overpassId);
			}
			wheres.add("u.id = ?");
			objs.add(userId);
			if(!wheres.isEmpty()) {
				sql.append(" where ");
				for(String where : wheres){
					sql.append(where).append(" and ");
				}
				sql = new StringBuilder(sql.substring(0, sql.length() - 4));
			}
			
			return jdbcTemplate.query(sql.toString(), new RowMapper<Overpass>(){
	
				@Override
				public Overpass mapRow(ResultSet rs, int rowNum) throws SQLException {
					Overpass o = new Overpass();
					o.setId(rs.getString("id"));
					o.setName(rs.getString("name"));
					o.setLocation(rs.getString("location"));
					o.setDistrictName(rs.getString("district_name"));
					o.setAmphurName(rs.getString("amphur_name"));
					o.setProvinceName(rs.getString("province_name"));
					o.setLatitude(rs.getString("latitude"));
					o.setLongtitude(rs.getString("longtitude"));
					o.setSetpointWatt(rs.getDouble("setpoint_watt"));
					o.setPostcode(rs.getString("postcode"));
					o.setProvince(rs.getInt("province"));
					o.setAmphur(rs.getInt("amphur"));
					o.setDistrict(rs.getInt("district"));
					o.setStatus(Status.valueOf(rs.getString("status")));
					o.setOverpassStatus(rs.getString("overpass_status"));
					return o;
				}
			}, objs.toArray());
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}

	@Override
	public List<LightBulb> getLightBulbAll() {
		try {
			List<LightBulb> objs = new ArrayList<>();
			StringBuilder sql = new StringBuilder();
			sql.append("select * from light_bulb");
			
			return jdbcTemplate.query(sql.toString(), new RowMapper<LightBulb>(){
	
				@Override
				public LightBulb mapRow(ResultSet rs, int rowNum) throws SQLException {
					LightBulb o = new LightBulb();
					o.setId(rs.getInt("id"));
					o.setLightName(rs.getString("light_name"));
					o.setWatt(rs.getDouble("watt"));
					return o;
				}
			}, objs.toArray());
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}

	@Override
	public List<OverpassStatus> getOverpassStatusByGroupId(String groupId) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select o.* from overpass_status o inner join map_group_overpass m on o.overpass_id = m.overpass_id and m.group_id = ? order by o.effective_date desc");
			
			return jdbcTemplate.query(sql.toString(), new RowMapper<OverpassStatus>(){
	
				@Override
				public OverpassStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
					OverpassStatus o = new OverpassStatus();
					o.setActive(rs.getString("active"));
					o.setAmphur(rs.getString("amphur"));
					o.setDistrict(rs.getString("district"));
					o.setEffectiveDate(rs.getTimestamp("effective_date"));
					o.setId(rs.getString("id"));
					o.setLatitude(rs.getString("latitude"));
					o.setLongtitude(rs.getString("longtitude"));
					o.setLocation(rs.getString("location"));
					o.setMapUrl(rs.getString("map_url"));
					o.setOverpassId(rs.getString("overpass_id"));
					o.setProvince(rs.getString("province"));
					o.setStatus(StatusLight.valueOf(rs.getString("status")));
					o.setWatt(rs.getDouble("watt"));
					o.setTopic(rs.getString("topic"));
					o.setLocationDisplay(rs.getString("location_display"));
					return o;
				}
			}, new Object[] { groupId });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}

}
