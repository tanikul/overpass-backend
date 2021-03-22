package com.overpass.reposiroty;

import java.util.List;

import com.overpass.model.AnswerOverpass;
import com.overpass.model.OverpassStatus;

public interface AnswerOverpassRepository {

	public List<AnswerOverpass> getAnswerByOverpassStatusId(int id);
	public void insertAnswerOverpass(AnswerOverpass answer);
	public OverpassStatus getOverpassStatusById(String id);
	
}
