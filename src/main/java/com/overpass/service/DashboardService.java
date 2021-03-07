package com.overpass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.overpass.model.Dashboard;

public interface DashboardService {

	public Dashboard getDataDashBoard();
	public boolean validateOverpass();
}
