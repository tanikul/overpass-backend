package com.overpass.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.overpass.model.AnswerOverpass;
import com.overpass.model.OverpassStatus;
import com.overpass.model.User;
import com.overpass.reposiroty.AnswerOverpassRepository;
import com.overpass.reposiroty.UserRepository;

@Service
public class AnswerOverpassServiceImpl implements AnswerOverpassService {

	@Autowired
	private AnswerOverpassRepository answerOverpassRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public List<AnswerOverpass> getAnswerByOverpassStatusId(int id) {
		return answerOverpassRepository.getAnswerByOverpassStatusId(id);
	}

	@Override
	public void insertAnswerOverpass(AnswerOverpass answer, Authentication authentication) {
		User u = userRepository.getUserByUsername(authentication.getName());
		answer.setCreateBy(u.getId());
		answerOverpassRepository.insertAnswerOverpass(answer);
		
	}

	@Override
	public OverpassStatus getOverpassStatusById(String id) {
		return answerOverpassRepository.getOverpassStatusById(id);
	}

}
