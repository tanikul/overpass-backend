package com.overpass.controller;

import java.util.List;

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
import com.overpass.common.Constants;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;
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
	public void insert(@RequestBody User data, Authentication authentication) throws Exception{
		try {
			userService.inserUser(data, authentication);	
		}catch(Exception ex) {
			throw ex;
		}
		
	}
	
	@PreAuthorize("hasAuthority(T(com.overpass.common.Constants).ADMIN) || hasAuthority(T(com.overpass.common.Constants).SUPER_ADMIN)")
	@PostMapping("/update")
	public void update(@RequestBody User data, Authentication authentication){
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
