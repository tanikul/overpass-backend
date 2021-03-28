package com.overpass.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchGroupOverpass {

	private int groupId;
	private String groupName;
	private String overpassId;
	private String location;
	private String overpassName;
	private String districtName;
	private String amphurName;
	private String provinceName;
	private String lineNotifyToken;
	private String email;
	
}
