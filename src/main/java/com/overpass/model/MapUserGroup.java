package com.overpass.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapUserGroup extends Entity {

	private int id;
	private int groupId;
	private int userId;
	
}
