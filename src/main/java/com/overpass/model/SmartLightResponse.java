package com.overpass.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmartLightResponse {

	@JsonProperty("id")
	private int id;
	
	@JsonProperty("ID_Overpass")
	private String idOverpass;
	
	@JsonProperty("Watt")
	private Double watt;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Timestamp")
	private Timestamp timestamp;

}
