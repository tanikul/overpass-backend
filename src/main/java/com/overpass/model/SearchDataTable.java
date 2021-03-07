package com.overpass.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDataTable<T> {

	private String sort;
	private String order;
	private int page;
	private int limit;
	private T filter;
}
