package com.overpass.common;

public class Constants {

	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String TH = "TH-th";
	public static final String EN = "en-US";
	public static final String SUPER_ADMIN = "SUPER_ADMIN";
	public static final String ADMIN = "ADMIN";
	public static final String USER = "USER";
	
	public enum Master {
		
		PREFIX,
		ROLE,
		STATUS,
		PROVINCE,
		AMPHUR,
		DISTRICT
	}
	
	public enum Status {
		ACTIVE,
		INACTIVE
	}
	
	public enum StatusLight {
		ON,
		OFF,
		WARNING
	}
	
}
