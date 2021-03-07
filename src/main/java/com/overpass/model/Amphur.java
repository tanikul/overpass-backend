package com.overpass.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Amphur {

	private int key;
	private String value;
	private String postCode;
	private List<District> district;
}
