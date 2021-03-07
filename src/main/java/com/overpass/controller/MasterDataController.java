package com.overpass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.overpass.common.Constants.Master;
import static com.overpass.common.Constants.ACCEPT_LANGUAGE;
import com.overpass.service.MasterDataService;

@RestController
@RequestMapping("api/master")
public class MasterDataController {

	@Autowired
	private MasterDataService masterDataService;
	
	@GetMapping(path = "/{type}")
	public ResponseEntity<?> getData(@PathVariable String type, @RequestParam(required = false) Integer id,
			@RequestHeader(name = ACCEPT_LANGUAGE) String locale) {
		if(id == null) {
			return  new ResponseEntity<>(masterDataService.getMasterData(Master.valueOf(type.toUpperCase()), locale), HttpStatus.OK);
		}
		return new ResponseEntity<>(masterDataService.getMasterDataById(Master.valueOf(type.toUpperCase()), id, locale), HttpStatus.OK);
	}
	
	@GetMapping(path = "/province")
	public ResponseEntity<?> getProvince(
			@RequestHeader(name = ACCEPT_LANGUAGE) String locale) {

		return new ResponseEntity<>(masterDataService.getProvince(locale), HttpStatus.OK);
	}
	
	@GetMapping(path = "/amphur")
	public ResponseEntity<?> getAmphur(@RequestParam(required = true) Integer provinceId,
			@RequestHeader(name = ACCEPT_LANGUAGE) String locale) {

		return new ResponseEntity<>(masterDataService.getAmphurByProvinceId(locale, provinceId), HttpStatus.OK);
	}
	
	@GetMapping(path = "/district")
	public ResponseEntity<?> getDistrict(@RequestParam(required = true) Integer amphurId,
			@RequestHeader(name = ACCEPT_LANGUAGE) String locale) {

		return new ResponseEntity<>(masterDataService.getDistrictByAmphurId(locale, amphurId), HttpStatus.OK);
	}
	
	@GetMapping(path = "/mappingAddress")
	public ResponseEntity<?> getMappingAddress(
			@RequestHeader(name = ACCEPT_LANGUAGE) String locale) {

		return new ResponseEntity<>(masterDataService.getMappingAddress(), HttpStatus.OK);
	}
}
