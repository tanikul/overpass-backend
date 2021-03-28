package com.overpass.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.overpass.model.Dashboard;

public interface DashboardService {

	public Dashboard getDataDashBoard(Authentication authentication);
	public Dashboard getDataDashBoard(Integer groupId);
	public List<Integer> validateOverpass();
}
