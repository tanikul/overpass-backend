package com.overpass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.overpass.model.AnswerOverpass;
import com.overpass.model.OverpassStatus;
import com.overpass.service.AnswerOverpassService;

@RestController
@RequestMapping("api/answer")
public class AnswerOverpassController {

	@Autowired
	private AnswerOverpassService answerOverpassService;
	
	@GetMapping("/getAnswerByOverpassStatusId")
	@ResponseBody
	public List<AnswerOverpass> getAnswerByOverpassStatusId(@RequestParam(value = "id") Integer id){
		return answerOverpassService.getAnswerByOverpassStatusId(id);
	}
	
	@PostMapping("/insertAnswerOverpass")
	public void insertAnswerOverpass(@RequestBody AnswerOverpass answer, Authentication authentication) throws Exception{
		try {
			answerOverpassService.insertAnswerOverpass(answer, authentication);
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	@GetMapping("/getOverpassStatusById")
	@ResponseBody
	public OverpassStatus getOverpassStatusById(@RequestParam(value = "id") String id){
		return answerOverpassService.getOverpassStatusById(id);
	}
}
