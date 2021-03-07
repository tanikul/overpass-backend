package com.overpass.model;

import java.sql.Timestamp;

import com.overpass.common.Constants.StatusLight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverpassStatus {

	private String overpassId;
	private StatusLight status;
	private Timestamp effectiveDate;
	private Double watt;
	private String active;
}
