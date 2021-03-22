package com.overpass.reposiroty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.overpass.common.Constants.StatusLight;
import com.overpass.model.AnswerOverpass;
import com.overpass.model.OverpassStatus;

@Repository
public class AnswerOverpassRepositoryImpl implements AnswerOverpassRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<AnswerOverpass> getAnswerByOverpassStatusId(int id) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select a.*, concat(u.prefix, u.first_name, ' ', u.last_name) create_by_name from answer_overpass a inner join users u on a.create_by = u.id where overpass_status_id = ? order by create_dt asc ");
			
			return jdbcTemplate.query(sql.toString(), new RowMapper<AnswerOverpass>(){
	
				@Override
				public AnswerOverpass mapRow(ResultSet rs, int rowNum) throws SQLException {
					AnswerOverpass o = new AnswerOverpass();
					o.setCreateByName(rs.getString("create_by_name"));
					o.setCreateBy(rs.getInt("create_by"));
					o.setCreateDt(rs.getDate("create_dt"));
					o.setFixed(rs.getString("fixed"));
					o.setFixedDate(rs.getTimestamp("fixed_date"));
					o.setId(rs.getInt("id"));
					o.setOverpassStatusId(rs.getInt("overpass_status_id"));
					o.setRootCuase(rs.getString("root_cuase"));
					o.setUserFixed(rs.getString("user_fixed"));
					return o;
				}
			}, new Object[] { id });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}

	@Override
	public void insertAnswerOverpass(AnswerOverpass answer) {
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("insert into answer_overpass (overpass_status_id,root_cuase, fixed, fixed_date, user_fixed, create_dt, create_by)");
			sql.append(" values(?, ?, ?, ?, ?, NOW(), ? )");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setInt(1, answer.getOverpassStatusId());
					ps.setString(2, answer.getRootCuase());
					ps.setString(3, answer.getFixed());
					ps.setTimestamp(4, answer.getFixedDate());
					ps.setString(5, answer.getUserFixed());
					ps.setInt(6, answer.getCreateBy());
					return ps.execute();
				}  
			});  
		} catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public OverpassStatus getOverpassStatusById(String id) {
	
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select o.* from overpass_status o where o.id = ? ");
			
			return jdbcTemplate.queryForObject(sql.toString(), new RowMapper<OverpassStatus>(){
	
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
			}, new Object[] { id });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}
}
