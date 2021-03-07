package com.overpass.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.overpass.model.MapGroupOverpassRequest;

@RestController
public class SubscribtionController {

	@PostMapping("/subscription")
	public ResponseEntity<?> insertMapGroupAndOverpass(){
		return new ResponseEntity<>("xxx", HttpStatus.OK);
	}
}
