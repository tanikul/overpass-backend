package com.overpass.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Entity {

	@JsonInclude(Include.NON_NULL)
	private Date createDt;
	
	@JsonInclude(Include.NON_NULL)
	private Date updateDt;
	
	@JsonInclude(Include.NON_DEFAULT)
	private int createBy;
	
	@JsonInclude(Include.NON_DEFAULT)
	private int updateBy;
}
