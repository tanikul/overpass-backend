package com.overpass.reposiroty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.overpass.model.LightBulb;

@Repository
public class LightBulbRepositoryImpl implements LightBulbRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<LightBulb> getLightBulbList() {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * from light_bulb order by light_name asc");
			return jdbcTemplate.query(sql.toString(), new RowMapper<LightBulb>(){
	
				@Override
				public LightBulb mapRow(ResultSet rs, int rowNum) throws SQLException {
					LightBulb light = new LightBulb();
					light.setId(rs.getInt("id"));
					light.setLightName(rs.getString("light_name"));
					light.setWatt(rs.getDouble("watt"));
					return light;
				}
			}, new Object[]{ });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }catch(Exception ex) {
	    	throw ex;
	    }	
	}

	@Override
	public void saveLightBulb(LightBulb light) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into light_bulb (light_name, watt)");
			sql.append(" values(?, ?)");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setString(1, light.getLightName());
					ps.setDouble(2, light.getWatt());
					return ps.execute();
				}  
			});  
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public void updateLightBulb(LightBulb light) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("update light_bulb set light_name = ?, watt = ? where id = ?");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setString(1, light.getLightName());
					ps.setDouble(2, light.getWatt());
					ps.setInt(3, light.getId());
					return ps.execute();
				}  
			});  
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public void deleteLightBulb(LightBulb light) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from light_bulb where id = ?");
			jdbcTemplate.execute(sql.toString(),new PreparedStatementCallback<Boolean>(){  
			 
	
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setInt(1, light.getId());
					return ps.execute();
				}  
			});  
		}catch(Exception ex) {
			throw ex;
		}
	}

}
