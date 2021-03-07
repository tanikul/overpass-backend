package com.overpass.reposiroty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.overpass.common.Constants;
import com.overpass.common.Constants.Status;
import com.overpass.model.GroupOverpass;
import com.overpass.model.MapGroupOverpass;
import com.overpass.model.MapUserGroup;
import com.overpass.model.Overpass;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.SearchGroupOverpass;
import com.overpass.model.User;

@Repository
public class MappingOverpassRepositoryImpl implements MappingOverpassRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Overpass> getOverPassByUserId(int userId) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select o.id, o.name from mapping_overpass m inner join overpass o on m.overpass_id = o.id where m.user_id = ? order by m.id asc ");
			return jdbcTemplate.query(sql.toString(), new RowMapper<Overpass>(){
	
				@Override
				public Overpass mapRow(ResultSet rs, int rowNum) throws SQLException {
					Overpass o = new Overpass();
					o.setId(rs.getString("id"));
					o.setName(rs.getString("name"));
					return o;
				}
			}, new Object[]{ userId });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    } catch(Exception ex) {
	    	throw ex;
	    }	
	}
	
	@Override
	public List<GroupOverpass> getMappingGroupOverPassAll() {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select m.id, m.group_name from group_overpass m order by m.id asc ");
			return jdbcTemplate.query(sql.toString(), new RowMapper<GroupOverpass>(){
	
				@Override
				public GroupOverpass mapRow(ResultSet rs, int rowNum) throws SQLException {
					GroupOverpass o = new GroupOverpass();
					o.setId(rs.getInt("id"));
					o.setGroupName(rs.getString("group_name"));
					return o;
				}
			}, new Object[]{ });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    } catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public void insertMapGroupAndOverpass(MapGroupOverpass data) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into map_group_overpass (overpass_id, group_id, create_dt, update_dt, create_by, update_by)");
			sql.append(" values(?, ?, NOW(), NOW(), ?, ? )");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setString(1, data.getOverpassId());
					ps.setInt(2, data.getGroupId());
					ps.setInt(3, data.getCreateBy());
					ps.setInt(4, data.getUpdateBy());
					return ps.execute();
				}  
			});  
		} catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public int insertGroupOverpass(GroupOverpass data) {
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into group_overpass (group_name, create_dt, update_dt, create_by, update_by)");
			sql.append(" values(?, NOW(), NOW(), ?, ? )");
			jdbcTemplate.update(new PreparedStatementCreator() {
			    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			    	PreparedStatement statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			    	statement.setString(1, data.getGroupName());
			    	statement.setInt(2, data.getCreateBy());
			    	statement.setInt(3, data.getUpdateBy());
			        return statement;
			      }
			    },  keyHolder);
					
		} catch(Exception ex) {
	    	throw ex;
	    }	
		return keyHolder.getKey().intValue();
	}

	@Override
	public void insertMapUserAndGroup(MapUserGroup data) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into map_user_group (group_id, user_id, create_dt, update_dt, create_by, update_by)");
			sql.append(" values(?, ?, NOW(), NOW(), ?, ? )");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setInt(1, data.getGroupId());
					ps.setInt(2, data.getUserId());
					ps.setInt(3, data.getCreateBy());
					ps.setInt(4, data.getUpdateBy());
					return ps.execute();
				}  
			});  
		} catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public boolean deleteMapGroupAndOverpassByGroupId(int id) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from map_group_overpass where group_id = ?");
			return jdbcTemplate.update(sql.toString(), new Object[] { id }) == 1;  
		} catch(Exception ex) {
	    	throw ex;
	    }
	}

	@Override
	public void deleteGroupOverpass(int id) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from group_overpass where id = ?");
			jdbcTemplate.update(sql.toString(), new Object[] { id });  
		} catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public void deleteMapUserAndGroup(int id) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from map_user_group where id = ?");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setInt(1, id);
					return ps.execute();
				}  
			});  
		} catch(Exception ex) {
	    	throw ex;
	    }
	}

	@Override
	public List<SearchGroupOverpass> getOverPassByGroupId(int groupId) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select o.id overpass_id, g.id group_id, g.group_name, o.setpoint_watt, o.min_watt, o.name, o.latitude, o.longtitude, o.location, o.district, o.amphur, o.province, o.postcode, d.district_name, a.amphur_name, p.province_name from map_group_overpass m ");
			sql.append(" inner join group_overpass g on g.id = m.group_id ");
			sql.append(" inner join overpass o on m.overpass_id = o.id ");
			sql.append(" left join district d on d.district_id = o.district");
			sql.append(" left join amphur a on a.amphur_id = o.amphur");
			sql.append(" left join province p on p.province_id = o.province");
			sql.append(" where m.group_id = ? order by m.group_id asc ");
			return jdbcTemplate.query(sql.toString(), new RowMapper<SearchGroupOverpass>(){
	
				@Override
				public SearchGroupOverpass mapRow(ResultSet rs, int rowNum) throws SQLException {
					SearchGroupOverpass o = new SearchGroupOverpass();
					o.setGroupId(rs.getInt("group_id"));
					o.setGroupName(rs.getString("group_name"));
					o.setLocation(rs.getString("location"));
					o.setOverpassId(rs.getString("overpass_id"));
					o.setOverpassName(rs.getString("name"));
					o.setProvinceName(rs.getString("province_name"));
					o.setAmphurName(rs.getString("amphur_name"));
					o.setDistrictName(rs.getString("district_name"));
					return o;
				}
			}, new Object[]{ groupId });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    } catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public List<SearchGroupOverpass> searchMappingOverPass(SearchDataTable<GroupOverpass> data) {
		StringBuilder sql = new StringBuilder();
		List<String> wheres = new ArrayList<>();
		List<SearchGroupOverpass> result = new ArrayList<>();
		
		try {
			
			sql.append("select g.id, g.group_name, o.id overpass_id, o.location, o.name overpass_name, d.district_name, a.amphur_name, p.province_name  from group_overpass g inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on m.overpass_id = o.id ");
			sql.append("left join province p on o.province = p.province_id ");
			sql.append("left join amphur a on o.amphur = a.amphur_id ");
			sql.append("left join district d on o.district = d.district_id ");
			if(data.getFilter() != null) {
				if(data.getFilter().getId() > 0) {
					wheres.add("g.id = ?");
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
			sql.append((StringUtils.isNotBlank(data.getSort())) ? data.getSort() : "id");
			sql.append(" ");
			sql.append((StringUtils.isNotBlank(data.getOrder())) ? data.getOrder() : "asc");
			sql.append(" limit " + data.getLimit() + " offset " + (data.getLimit() * data.getPage()));
			
			result = jdbcTemplate.query(sql.toString(), 
					new PreparedStatementSetter() {
				   
		         public void setValues(PreparedStatement preparedStatement) throws SQLException {
		        	 GroupOverpass u = data.getFilter();
		        	int i = 1;
		        	if(data.getFilter() != null && data.getFilter().getId() > 0) {
		 				preparedStatement.setInt(i++, u.getId());
		 			}
		         }
			},
		         new RowMapper<SearchGroupOverpass>(){
				
				@Override
				public SearchGroupOverpass mapRow(ResultSet rs, int rowNum) throws SQLException {
					SearchGroupOverpass s = new SearchGroupOverpass();
					s.setGroupId(rs.getInt("id"));
					s.setOverpassId(rs.getString("overpass_id"));
					s.setGroupName(rs.getString("group_name"));
					s.setLocation(rs.getString("location"));
					s.setOverpassName(rs.getString("overpass_name"));
					s.setProvinceName(rs.getString("province_name"));
					s.setAmphurName(rs.getString("amphur_name"));
					s.setDistrictName(rs.getString("district_name"));
					return s;
				}
			});
		}catch(Exception ex) {
			throw ex;
		}
		return result;
	}
	
	@Override
	public int count(SearchDataTable<GroupOverpass> data) {
		StringBuilder sql = new StringBuilder();
		List<String> wheres = new ArrayList<>();
		try {
			sql.append("select count(0) from group_overpass g inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on m.overpass_id = o.id ");
			sql.append("left join province p on o.province = p.province_id ");
			sql.append("left join amphur a on o.amphur = a.amphur_id ");
			sql.append("left join district d on o.district = d.district_id ");
			if(data.getFilter() != null) {
				if(data.getFilter().getId() > 0) {
					wheres.add("g.id = ?");
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
			GroupOverpass u = data.getFilter();
			
			if(data.getFilter() != null && data.getFilter().getId() > 0) {
				objs.add(u.getId());
 			}
			return jdbcTemplate.queryForObject(sql.toString(), Integer.class, objs.toArray());
		}catch(Exception ex) {
			throw ex;
		}
	}

}
