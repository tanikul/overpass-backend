package com.overpass.model;

import com.overpass.common.Constants.StatusLight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostNotificationData {

	private String body;
	private String title;
	private StatusLight status;
}
