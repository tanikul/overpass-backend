package com.overpass.model;

import com.overpass.common.Constants.StatusLight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageToNotify {

	private String topic;
	private String location;
	private String note;
	private String timeToHang;
	private String coordinate;
	private StatusLight status;
	private String id;
}
