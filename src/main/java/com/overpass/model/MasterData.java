package com.overpass.model;

import com.overpass.common.Constants.Master;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasterData {

	private int id;
	private Master type;
	private String nameTh;
	private String nameEn;
	
}
