package com.overpass.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.overpass.common.Constants;
import com.overpass.common.Constants.Status;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;
import com.overpass.service.FileStorageService;
import com.overpass.service.UserService;

@RestController
@RequestMapping("api/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/{id}")
	public User getUserById(@PathVariable int id) {
		return userService.getUserById(id);
	}
	
	@GetMapping("/getUserByAuthen")
	public User getUser(Authentication authentication) {
		return userService.getUser(authentication);
	}
	
	@PostMapping("/searchUser")
	public ResponseDataTable<User> searchUser(@RequestBody SearchDataTable<User> data, Authentication authentication) throws Exception{
		return userService.searchUser(data, authentication);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/save")
	public void insert(
			@RequestParam("username") String username,
			@RequestParam("prefix") String prefix,
			@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("role") String role,
			@RequestParam("email") String email,
			@RequestParam("lineId") String lineId,
			@RequestParam("mobileNo") String mobileNo,
			@RequestParam("groupId") int groupId,
			@RequestParam("status") Status status,
			@RequestParam(value="imageProfile", required=false) MultipartFile imageProfile,
			Authentication authentication) throws Exception{
		try {
			
			User data = new User();
			data.setUsername(username);
			data.setPrefix(prefix);
			data.setFirstName(firstName);
			data.setLastName(lastName);
			data.setRole(role);
			data.setEmail(email);
			data.setLineId(lineId);
			data.setMobileNo(mobileNo);
			data.setGroupId(groupId);
			data.setStatus(status);
			userService.inserUser(data, imageProfile, authentication);	
		}catch(Exception ex) {
			throw ex;
		}
		
	}
	
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/update")
	public void update(@RequestParam("username") String username,
			@RequestParam("prefix") String prefix,
			@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("role") String role,
			@RequestParam("email") String email,
			@RequestParam("lineId") String lineId,
			@RequestParam("mobileNo") String mobileNo,
			@RequestParam("groupId") int groupId,
			@RequestParam("status") Status status,
			@RequestParam(value="imageProfile", required=false) MultipartFile imageProfile, Authentication authentication){
		User data = new User();
		data.setUsername(username);
		data.setPrefix(prefix);
		data.setFirstName(firstName);
		data.setLastName(lastName);
		data.setRole(role);
		data.setEmail(email);
		data.setLineId(lineId);
		data.setMobileNo(mobileNo);
		data.setGroupId(groupId);
		data.setStatus(status);
		userService.updateUser(data, authentication);
	}
	
	@PostMapping("/delete")
	public void insert(int id){
		userService.deleteUser(id);
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@GetMapping
	public List<User> getUserByRole(@RequestParam("role") String role) {
		return userService.getUserByRole(role);
	}
	
	@PostMapping("/changePassword")
	public void changePassword(Authentication authentication, String newPassword) {
		userService.changePassword(authentication, newPassword);
	}
	
	@PostMapping("/updateUserProfile")
	public void updateUserProfile(@RequestBody User data, Authentication authentication){
		userService.updateUserProfile(data, authentication);
	}
}
