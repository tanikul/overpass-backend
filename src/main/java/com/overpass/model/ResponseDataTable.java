package com.overpass.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDataTable<T> {

	private int totalRecords;
	private List<T> data = new ArrayList<>();
}
