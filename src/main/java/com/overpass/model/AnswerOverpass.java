package com.overpass.model;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerOverpass {

	private int id;
	private int overpassStatusId;
	private String rootCuase;
	private String fixed;
	private Timestamp fixedDate;
	private String userFixed;
	private Date createDt;
	private int createBy;
	private String createByName;
}
