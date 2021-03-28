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
	public Map<String, Object> countOverpassByZone(int groupId) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select (select update_dt from overpass o order by update_dt desc limit 1) update_dt, ");
			sql.append("sum(cnt) cnt from (select 1 cnt from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id where g.id = ? ");
			sql.append("group by o.province, o.amphur, o.district) a ");
			return jdbcTemplate.queryForMap(sql.toString(), new Object[] { groupId });
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public Map<String, Object> countOverpassAll(int groupId) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select (select effective_date from overpass_status order by effective_date desc limit 1) effective_date, ");
			sql.append("sum(cnt) cnt from  (");
			sql.append("select 1 cnt from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id ");
			sql.append("left join overpass_status s on s.overpass_id = o.id and s.active = 'Y' where g.id = ? ");
			sql.append(") a");
			return jdbcTemplate.queryForMap(sql.toString(), new Object[] { groupId });
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public Map<String, Object> getOverpassOnOff(int groupId) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select * from ( ");
			sql.append("select count(0) `ON` from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id ");
			sql.append("inner join overpass_status s on s.overpass_id = o.id and s.active = 'Y' and s.status = 'ON' where g.id = ? ");
			sql.append(") `on`, ");
			sql.append("(select count(0) `ON` from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id ");
			sql.append("inner join overpass_status s on s.overpass_id = o.id and s.active = 'Y' and s.status in ('OFF', 'WARNING') where g.id = ? ");
			sql.append(") off, ");
			sql.append("(");
			sql.append("select effective_date effective_date_on from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id ");
			sql.append("inner join overpass_status s on s.overpass_id = o.id and s.status = 'ON' where g.id = ? ");
			sql.append("order by effective_date desc limit 1 ");
			sql.append(") effective_date_on, ");
			sql.append("(");
			sql.append("select effective_date effective_date_off from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id ");
			sql.append("inner join overpass_status s on s.overpass_id = o.id and s.status in ('OFF', 'WARNING') where g.id = ? ");
			sql.append("order by effective_date desc limit 1 ");
			sql.append(") effective_date_off ");
			return jdbcTemplate.queryForMap(sql.toString(), new Object[] { groupId, groupId, groupId, groupId  });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}

	@Override
	public Map<String, Object> getOverpassByMonth(StatusLight status, int groupId) {
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
			sql.append("from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id "); 
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id "); 
			sql.append("inner join overpass_status s on s.overpass_id = o.id"); 
			if(status.equals(StatusLight.OFF)) {
				sql.append(" and s.status in ('OFF', 'WARNING')");
			}else {
				sql.append(" and s.status = 'ON'");
			}
			sql.append(" where g.id = ? ");
			if(status.equals(StatusLight.OFF)) {
				sql.append(" and s.id not in (select id from overpass_status where seq = 1 and status = 'OFF') ");
			}else {
				sql.append(" and s.overpass_id not in (select overpass_id from overpass_status where seq > 1 and status = 'OFF' union select overpass_id from overpass_status where seq = 1 and status = 'WARNING')");
			}
			return jdbcTemplate.queryForMap(sql.toString(), new Object[] { groupId });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }	
	}
	
	@Override
	public Map<String, Object> countOverpassAllByStatus(StatusLight status, int groupId) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select (");
			sql.append("select effective_date from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id ");
			sql.append("inner join overpass_status s on s.overpass_id = o.id and ");
			if(StatusLight.OFF.equals(status)) {
				sql.append("s.status in ('OFF', 'WARNING') ");
			}else {
				sql.append("s.status in ('ON') ");
			}
			sql.append(" where g.id = ? order by effective_date desc limit 1 ");
			sql.append(") effective_date, (");
			sql.append("select count(0) from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id ");
			sql.append("inner join overpass_status s on s.overpass_id = o.id and ");
			if(StatusLight.OFF.equals(status)) {
				sql.append("s.status in ('OFF', 'WARNING') ");
			}else {
				sql.append("s.status in ('ON') ");
			}
			sql.append("and s.active = 'Y' where g.id = ? ");
			sql.append(") cnt");
			return jdbcTemplate.queryForMap(sql.toString(), new Object[] { groupId, groupId });
		} catch (EmptyResultDataAccessException e) {
	        return null;
	    }catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public List<Map<String, Object>> getDataDonutChart(int groupId) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select s.amphur amphur_name, o.amphur amphur_id, count(s.amphur) cnt from ");
			sql.append("group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id ");
			sql.append("inner join overpass o on m.overpass_id = o.id ");
			sql.append("inner join overpass_status s on o.id = s.overpass_id and s.`status` in ('OFF', 'WARNING') ");
			sql.append("where o.`status` = 'ACTIVE' and o.province = 1 and g.id = ? ");
			sql.append(" and s.id not in (select id from overpass_status where seq = 1 and status = 'OFF') ");
			sql.append("group by s.amphur, o.amphur");
			return jdbcTemplate.queryForList(sql.toString(), new Object[] { groupId });
		}catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public Integer getSeqOverpassStatusByOverpassIdAndStatus(String overpassId, StatusLight status) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append(" select seq from overpass_status where overpass_id = ? and DATE(effective_date) = DATE(NOW()) and status = ?");
			return jdbcTemplate.queryForObject(sql.toString(), Integer.class, new Object[] { overpassId, status.name() });
		} catch (EmptyResultDataAccessException e) {
	        return 1;
	    }catch(Exception ex) {
			throw ex;
		}
	}

	@Override
	public Integer getMaxOverpassByStatus(int groupId, StatusLight status) {
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select count(*) cnt " );
			sql.append("from group_overpass g ");
			sql.append("inner join map_group_overpass m on g.id = m.group_id "); 
			sql.append("inner join overpass o on o.status = 'ACTIVE' and m.overpass_id = o.id "); 
			sql.append("inner join overpass_status s on s.overpass_id = o.id"); 
			if(status.equals(StatusLight.OFF)) {
				sql.append(" and s.status in ('OFF', 'WARNING')");
			}else {
				sql.append(" and s.status = 'ON'");
			}
			sql.append(" where g.id = ? ");
			if(status.equals(StatusLight.OFF)) {
				sql.append(" and s.id not in (select id from overpass_status where seq = 1 and status = 'OFF') ");
			}else {
				sql.append(" and s.overpass_id not in (select overpass_id from overpass_status where seq > 1 and status = 'OFF' union select overpass_id from overpass_status where seq = 1 and status = 'WARNING')");
			}
			return jdbcTemplate.queryForObject(sql.toString(), Integer.class, new Object[] { groupId });
		} catch (EmptyResultDataAccessException e) {
	        return 0;
	    }	
	}

}
