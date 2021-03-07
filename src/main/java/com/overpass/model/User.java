package com.overpass.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.overpass.common.Constants.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends Entity implements Serializable {

	private static final long serialVersionUID = 376076176827483820L;
	private int id;
	private String username;
	
	@JsonInclude(Include.NON_NULL)
	private String password;
	private String prefix;
	private int prefixId;
	private String firstName;
	private String lastName;
	private String role;
	private Status status;
	private String email;
	private String lineId;
	private String mobileNo;
	private int groupId;
	private String groupName;
	
}
