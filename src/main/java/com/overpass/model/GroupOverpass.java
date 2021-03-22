package com.overpass.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupOverpass extends Entity {

	private int id;
	private String groupName;
	private String lineNotiToken;
}
