package com.overpass.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmartLight {
	
	private int status;
	private String error;
	private List<SmartLightResponse> response;
	
	
}
