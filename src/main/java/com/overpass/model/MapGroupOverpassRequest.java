package com.overpass.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapGroupOverpassRequest {

	private String groupName;
	private int groupId;
	private List<Overpass> overpasses;
}
