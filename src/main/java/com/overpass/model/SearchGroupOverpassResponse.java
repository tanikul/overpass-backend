package com.overpass.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchGroupOverpassResponse {

	private String groupName;
	private int groupId;
	private String lineNotifyToken;
	private String email;
	private List<Overpass> overpasses; 
}
