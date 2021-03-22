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

import com.overpass.common.Constants;
import com.overpass.common.Constants.Status;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;

@Repository
public class UserRepositoryImpl implements UserRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	 
	@Override
	public User checkLogin(String username) {
		try {
			return jdbcTemplate.queryForObject("SELECT USERNAME, PASSWORD, u.ROLE, u.GROUP_ID FROM users  u WHERE USERNAME = ? AND u.STATUS = 'ACTIVE'", new RowMapper<User>(){

				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setUsername(rs.getString("USERNAME"));
					user.setPassword(rs.getString("PASSWORD"));
					user.setRole(rs.getString("ROLE"));
					user.setGroupId(rs.getInt("GROUP_ID"));
					return user;
				}
			}, new Object[]{ username });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    } catch(Exception ex) {
	    	throw ex;
	    }	
	}
	
	@Override
	public User getUserById(int id) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT u.id, u.username, u.prefix, u.first_name, g.group_name, u.last_name, u.role, u.status, u.email, u.line_id, u.mobile_no FROM users u ");
			sql.append("INNER JOIN master_data m ON m.TYPE = 'PREFIX' AND u.prefix = m.ID ");
			sql.append("INNER JOIN group_overpass g ON u.group_id = g.id ");
			sql.append("WHERE u.id = ?");
			return jdbcTemplate.queryForObject(sql.toString(), new RowMapper<User>(){
	
				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setUsername(rs.getNString("username"));
					user.setPrefix(rs.getString("prefix"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setRole(rs.getString("role"));
					user.setStatus(Status.valueOf(rs.getString("status")));
					user.setEmail(rs.getString("email"));
					user.setLineId(rs.getString("line_id"));
					user.setMobileNo(rs.getString("mobile_no"));
					user.setGroupName(rs.getString("group_name"));
					return user;
				}
			}, new Object[]{ id });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    } catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public void inserUser(User user) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into users (username, password, prefix, first_name, last_name, role, status, email, line_id, mobile_no, create_dt, update_dt, create_by, update_by, group_id)");
			sql.append(" values(?, ? , ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?, ? )");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setString(1, user.getUsername());
					ps.setString(2, user.getPassword());
					ps.setString(3, user.getPrefix());
					ps.setString(4, user.getFirstName());
					ps.setString(5, user.getLastName());
					ps.setString(6, user.getRole());
					ps.setString(7, user.getStatus().name());
					ps.setString(8, user.getEmail());
					ps.setString(9, user.getLineId());
					ps.setString(10, user.getMobileNo());
					ps.setInt(11, user.getCreateBy());
					ps.setInt(12, user.getUpdateBy());
					ps.setInt(13, user.getGroupId());
					return ps.execute();
				}  
			});  
		} catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public void updateUser(User user) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("update users set prefix = ?, first_name = ?, last_name = ?, role = ?, status = ?, email = ?, line_id = ?, mobile_no = ?, update_dt = NOW(), update_by = ?, group_id = ? where id = ?");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setString(1, user.getPrefix());
					ps.setString(2, user.getFirstName());
					ps.setString(3, user.getLastName());
					ps.setString(4, user.getRole());
					ps.setString(5, user.getStatus().name());
					ps.setString(6, user.getEmail());
					ps.setString(7, user.getLineId());
					ps.setString(8, user.getMobileNo());
					ps.setInt(9, user.getUpdateBy());
					ps.setInt(10, user.getGroupId());
					ps.setInt(11, user.getId());
					
					return ps.execute();
				}  
			});  
		}catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public void deleteUser(int id) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from users where id = ?");
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
	public ResponseDataTable<User> searchUser(SearchDataTable<User> data, String role, String createBy) {
		
		StringBuilder sql = new StringBuilder();
		List<String> wheres = new ArrayList<>();
		List<User> result = new ArrayList<User>();
		ResponseDataTable<User> response = new ResponseDataTable<User>();
		
		try {
			if(role.equals(Constants.USER)) return response;
			sql.append("select u.id, u.prefix prefix_id, u.username, m.name_th prefix, u.group_id, g.group_name, u.first_name, u.last_name, u.role, u.status, u.email, u.line_id, u.mobile_no ");
			
			sql.append("from users u inner join master_data m on m.type = 'PREFIX' and u.prefix = m.ID ");
			sql.append("inner join group_overpass g on u.group_id = g.id ");
			if(role.equals(Constants.ADMIN)) {
				wheres.add("u.role = '" + Constants.USER + "'");
			}
		
			if(data.getFilter() != null) {
				if(StringUtils.isNotBlank(data.getFilter().getUsername())) {
					wheres.add("u.username like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getFirstName())) {
					wheres.add("u.first_name like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getLastName())) {
					wheres.add("u.last_name like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getRole())) {
					wheres.add("u.role = ?");
				}
				if(data.getFilter().getStatus() != null) {
					wheres.add("u.status = ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getEmail())) {
					wheres.add("u.email like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getLineId())) {
					wheres.add("u.line_id like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getMobileNo())) {
					wheres.add("u.mobile_no like ?");
				}
				if(data.getFilter().getGroupId() != 0) {
					wheres.add("u.group_id = ?");
				}
			}
			wheres.add("username != ?");
			if(!wheres.isEmpty()) {
				sql.append(" where ");
				for(String where : wheres){
					sql.append(where).append(" and ");
				}
				sql = new StringBuilder(sql.substring(0, sql.length() - 4));
			}
			
			sql.append(" order by ");
			sql.append((StringUtils.isNotBlank(data.getSort())) ? getFieldOrderBy(data.getSort()) : "id");
			sql.append(" ");
			sql.append((StringUtils.isNotBlank(data.getOrder())) ? data.getOrder() : "asc");
			sql.append(" limit " + data.getLimit() + " offset " + (data.getLimit() * data.getPage()));
			
			result = jdbcTemplate.query(sql.toString(), 
					new PreparedStatementSetter() {
				   
		         public void setValues(PreparedStatement preparedStatement) throws SQLException {
		        	User u = data.getFilter();
		        	int i = 1;
		        	
		        	if(StringUtils.isNotBlank(data.getFilter().getUsername())) {
		 				preparedStatement.setString(i++, "%" + u.getUsername() + "%");
		 			}
		 			if(StringUtils.isNotBlank(data.getFilter().getFirstName())) {
		 				preparedStatement.setString(i++, "%" + u.getFirstName() + "%");
		 			}
		 			if(StringUtils.isNotBlank(data.getFilter().getLastName())) {
		 				preparedStatement.setString(i++, "%" + u.getLastName() + "%");
		 			}
		 			if(StringUtils.isNotBlank(data.getFilter().getRole())) {
		 				preparedStatement.setString(i++, u.getRole());
		 			}
		 			if(data.getFilter().getStatus() != null) {
		 				preparedStatement.setString(i++, u.getStatus().name());
		 			}
		 			if(StringUtils.isNotBlank(data.getFilter().getEmail())) {
		 				preparedStatement.setString(i++, "%" + u.getEmail() + "%");
		 			}
		 			if(StringUtils.isNotBlank(data.getFilter().getLineId())) {
		 				preparedStatement.setString(i++, "%" + u.getLineId() + "%");
		 			}
		 			if(StringUtils.isNotBlank(data.getFilter().getMobileNo())) {
		 				preparedStatement.setString(i++, "%" + u.getMobileNo() + "%");
		 			}
		 			if(data.getFilter().getGroupId() != 0) {
		 				preparedStatement.setInt(i++, u.getGroupId());
					}
		 			
		 			preparedStatement.setString(i++, createBy);
		         }
			},
		         new RowMapper<User>(){
				
				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setUsername(rs.getNString("username"));
					user.setPrefix(rs.getString("prefix"));
					user.setPrefixId(rs.getInt("prefix_id"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setRole(rs.getString("role"));
					user.setStatus(Status.valueOf(rs.getString("status")));
					user.setEmail(rs.getString("email"));
					user.setLineId(rs.getString("line_id"));
					user.setMobileNo(rs.getString("mobile_no"));
					user.setGroupId(rs.getInt("group_id"));
					user.setGroupName(rs.getString("group_name"));
					return user;
				}
			});
			response.setData(result);
			response.setTotalRecords(count(data, role, createBy));
		}catch(Exception ex) {
			throw ex;
		}
		return response;
	}
	
	private int count(SearchDataTable<User> data, String role, String createBy) {
		StringBuilder sql = new StringBuilder();
		List<String> wheres = new ArrayList<>();
		try {
			sql.append("select count(0) from users u inner join master_data m on m.type = 'PREFIX' and u.prefix = m.ID ");
			sql.append("inner join group_overpass g on g.id = u.group_id ");
			if(role.equals(Constants.ADMIN)) {
				wheres.add("u.role = '" + Constants.USER + "'");
			}
			
			if(data.getFilter() != null) {
				if(StringUtils.isNotBlank(data.getFilter().getUsername())) {
					wheres.add("u.username like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getFirstName())) {
					wheres.add("u.first_name like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getLastName())) {
					wheres.add("u.last_name like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getRole())) {
					wheres.add("u.role = ?");
				}
				if(data.getFilter().getStatus() != null) {
					wheres.add("u.status = ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getEmail())) {
					wheres.add("u.email like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getLineId())) {
					wheres.add("u.line_id like ?");
				}
				if(StringUtils.isNotBlank(data.getFilter().getMobileNo())) {
					wheres.add("u.mobile_no like ?");
				}
				if(data.getFilter().getGroupId() != 0) {
					wheres.add("u.group_id = ?");
				}
			}
			wheres.add("username != ?");
			if(!wheres.isEmpty()) {
				sql.append(" where ");
				for(String where : wheres){
					sql.append(where).append(" and ");
				}
				sql = new StringBuilder(sql.substring(0, sql.length() - 4));
			}
			List<Object> objs = new ArrayList<Object>();
			User u = data.getFilter();
			
			if(StringUtils.isNotBlank(data.getFilter().getUsername())) {
				objs.add("%" + u.getUsername() + "%");
 			}
 			if(StringUtils.isNotBlank(data.getFilter().getFirstName())) {
 				objs.add("%" + u.getFirstName() + "%");
 			}
 			if(StringUtils.isNotBlank(data.getFilter().getLastName())) {
 				objs.add("%" + u.getLastName() + "%");
 			}
 			if(StringUtils.isNotBlank(data.getFilter().getRole())) {
 				objs.add(u.getRole());
 			}
 			if(data.getFilter().getStatus() != null) {
 				objs.add(u.getStatus().name());
 			}
 			if(StringUtils.isNotBlank(data.getFilter().getEmail())) {
 				objs.add("%" + u.getEmail() + "%");
 			}
 			if(StringUtils.isNotBlank(data.getFilter().getLineId())) {
 				objs.add("%" + u.getLineId() + "%");
 			}
 			if(StringUtils.isNotBlank(data.getFilter().getMobileNo())) {
 				objs.add("%" + u.getMobileNo() + "%");
 			}
 			if(data.getFilter().getGroupId() != 0) {
 				objs.add(u.getGroupId());
			}
 			
 			objs.add(createBy);
			return jdbcTemplate.queryForObject(sql.toString(), Integer.class, objs.toArray());
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	@Override
	public int countByUsername(String username) {
		String sqlCount = "select count(0) from users where username = ?";
		return jdbcTemplate.queryForObject(sqlCount, Integer.class, new Object[] { username });
	}
	
	@Override
	public User getUserByUsername(String username) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT u.id, u.username, m.name_th prefix, u.group_id, g.group_name, u.first_name, u.last_name, u.role, u.status, u.email, u.line_id, u.mobile_no FROM users u ");
			sql.append("INNER JOIN master_data m ON m.TYPE = 'PREFIX' AND u.prefix = m.ID ");
			sql.append("inner join group_overpass g on g.id = u.group_id ");
			sql.append("WHERE u.username = ?");
			return jdbcTemplate.queryForObject(sql.toString(), new RowMapper<User>(){
	
				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setUsername(rs.getNString("username"));
					user.setPrefix(rs.getString("prefix"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setRole(rs.getString("role"));
					user.setStatus(Status.valueOf(rs.getString("status")));
					user.setEmail(rs.getString("email"));
					user.setLineId(rs.getString("line_id"));
					user.setMobileNo(rs.getString("mobile_no"));
					user.setGroupId(rs.getInt("group_id"));
					user.setGroupName(rs.getString("group_name"));
					
					return user;
				}
			}, new Object[]{ username });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    } catch(Exception ex) {
	    	throw ex;
	    }	
	}
	
	private String getFieldOrderBy(String column) {
		
		Map<String, String> map = new HashMap<>();
		map.put("firstName", "first_name");
		map.put("lastName", "last_name");
		map.put("lineId", "line_id");
		map.put("mobileNo", "mobile_no");
		if(map.containsKey(column)) return map.get(column);
		return column;
	}

	@Override
	public List<User> getUserByRole(String role) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT u.id, u.username, m.name_th prefix, u.first_name, u.last_name, u.role, u.status, u.email, u.line_id, u.mobile_no FROM users u ");
			sql.append("INNER JOIN master_data m ON m.TYPE = 'PREFIX' AND u.prefix = m.ID ");
			sql.append("WHERE u.role = ?");
			return jdbcTemplate.query(sql.toString(), new RowMapper<User>(){
	
				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setPrefix(rs.getString("prefix"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					return user;
				}
			}, new Object[]{ role });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public void changePassword(int id, String newPassword) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("update users set password = ?, update_dt = NOW(), update_by = ? where id = ?");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setString(1, newPassword);
					ps.setInt(2, id);
					ps.setInt(3, id);
					return ps.execute();
				}  
			});  
		}catch(Exception ex) {
	    	throw ex;
	    }	
	}
}
