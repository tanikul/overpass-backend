package com.overpass.model;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Province {

	private int key;
	private String value;
	private List<Amphur> amphur;
}
