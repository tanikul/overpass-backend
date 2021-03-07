package com.overpass.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapGroupOverpass extends Entity {

	private int id;
	private String overpassId;
	private int groupId;
}
