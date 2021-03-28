package com.overpass.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapGroupOverpassRequest {

	private String groupName;
	private int groupId;
	private String email;
	private String lineNotiToken;
	private List<Overpass> overpasses;
}
