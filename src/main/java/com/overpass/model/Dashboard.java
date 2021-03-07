package com.overpass.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dashboard {

	private Map<String, Object> overpassAll;
	private Map<String, Object> overpassByZone;
	private Map<String, Object> overpassOnByMonth;
	private Map<String, Object> overpassOffByMonth;
	private Map<String, Object> overpassOn;
	private Map<String, Object> overpassOff;
}