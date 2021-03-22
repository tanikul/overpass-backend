package com.overpass.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.overpass.common.Constants.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Overpass extends Entity {

	
	private String id;
	private Double setpointWatt;
	private String name;
	private String latitude;
	private String longtitude;
	private String location;

	@JsonInclude(Include.NON_DEFAULT)
	private int district;
	
	@JsonInclude(Include.NON_DEFAULT)
	private int amphur;
	
	private String postcode;
	
	@JsonInclude(Include.NON_DEFAULT)
	private int province;
	private String provinceName;
	private String amphurName;
	private String districtName;
	private Status status;
	
	private int lightBulbCnt;
	private int lightBulbId;
	private LightBulb lightBulb;
	
	@JsonInclude(Include.NON_NULL)
	private String overpassStatus;
	
}
