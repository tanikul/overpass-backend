package com.overpass.reposiroty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.overpass.common.Constants.StatusLight;
import com.overpass.model.Overpass;

@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Map<String, Object> countOverpassByZone() {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select (select update_dt from overpass o order by update_dt desc limit 1) update_dt, sum(cnt) cnt from (select count(0) cnt from overpass o where o.status = 'ACTIVE' group by o.province, o.amphur, o.district) a");
			return jdbcTemplate.queryForMap(sql.toString());
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public Map<String, Object> countOverpassAll() {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select (select effective_date from overpass_status order by effective_date desc limit 1) effective_date, count(a.overpass_id) cnt from	(select overpass_id from overpass_status group by overpass_id) a");
			return jdbcTemplate.queryForMap(sql.toString());
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public Map<String, Object> getOverpassOnOff() {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select * from (select count(0) `ON`from overpass_status o where o.status = 'on') `on`,");
			sql.append("(select count(0) off from overpass_status o where o.status = 'OFF') off, ");
			sql.append("(select effective_date effective_date_on from overpass_status where status = 'ON' order by effective_date desc limit 1) effective_date_on, ");
			sql.append("(select effective_date effective_date_off from overpass_status where status = 'OFF' order by effective_date desc limit 1) effective_date_off ");
			return jdbcTemplate.queryForMap(sql.toString());
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}

	@Override
	public Map<String, Object> getOverpassByMonth(StatusLight status) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select " );
			sql.append("sum(if(month(effective_date) = 1, 1, 0))  AS Jan, ");
			sql.append("sum(if(month(effective_date) = 2, 1, 0))  AS Feb, ");
			sql.append("sum(if(month(effective_date) = 3, 1, 0))  AS Mar, ");
			sql.append("sum(if(month(effective_date) = 4, 1, 0))  AS Apr, ");
			sql.append("sum(if(month(effective_date) = 5, 1, 0))  AS May, ");
			sql.append("sum(if(month(effective_date) = 6, 1, 0))  AS Jun, ");
			sql.append("sum(if(month(effective_date) = 7, 1, 0))  AS Jul, ");
			sql.append("sum(if(month(effective_date) = 8, 1, 0))  AS Aug, ");
			sql.append("sum(if(month(effective_date) = 9, 1, 0))  AS Sep, ");
			sql.append("sum(if(month(effective_date) = 10, 1, 0)) AS Oct, ");
			sql.append("sum(if(month(effective_date) = 11, 1, 0)) AS Nov, ");
			sql.append("sum(if(month(effective_date) = 12, 1, 0)) AS `Dec` ");
			sql.append("from overpass_status o where status = ?");
			
			return jdbcTemplate.queryForMap(sql.toString(), new Object[] { status });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}
	
	@Override
	public Map<String, Object> countOverpassAllByStatus(StatusLight status) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select (select effective_date from overpass_status where status = ? order by effective_date desc limit 1) effective_date, count(a.overpass_id) cnt from	(select overpass_id from overpass_status where status = ? group by overpass_id) a");
			return jdbcTemplate.queryForMap(sql.toString(), new Object[] { status, status });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }catch(Exception ex) {
			throw ex;
		}
	}

}
