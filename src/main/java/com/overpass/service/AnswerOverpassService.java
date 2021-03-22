package com.overpass.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.overpass.model.AnswerOverpass;
import com.overpass.model.OverpassStatus;

public interface AnswerOverpassService {

	public List<AnswerOverpass> getAnswerByOverpassStatusId(int id);
	public void insertAnswerOverpass(AnswerOverpass answer, Authentication authentication);
	public OverpassStatus getOverpassStatusById(String id);
}
