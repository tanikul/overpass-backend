package com.overpass.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostNotification {

	private String to;
	private MessageToNotify data;
}
