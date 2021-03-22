package com.overpass.model;

import java.sql.Timestamp;

import com.overpass.common.Constants.StatusLight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverpassStatus {

	private String id;
	private String overpassId;
	private StatusLight status;
	private Timestamp effectiveDate;
	private Double watt;
	private String active;
	private String location;
	private String district;
	private String amphur;
	private String province;
	private String latitude;
	private String longtitude;
	private String mapUrl;
	private String topic;
	private String locationDisplay;
}
