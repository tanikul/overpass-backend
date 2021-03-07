package com.overpass.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;

public interface UserService {

	public User getUserById(int id);
	public void inserUser(User user, Authentication authentication) throws Exception;
	public void updateUser(User user, Authentication authentication);
	public void deleteUser(int id);
	public ResponseDataTable<User> searchUser(SearchDataTable<User> data, Authentication authentication);
	public List<User> getUserByRole(String role);
}
