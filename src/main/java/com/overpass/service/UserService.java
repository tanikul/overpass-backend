package com.overpass.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;

public interface UserService {

	public User getUser(Authentication authentication);
	public User getUserById(int id);
	public void inserUser(User user, MultipartFile imageProfile, Authentication authentication) throws Exception;
	public void updateUser(User user, Authentication authentication);
	public void deleteUser(int id);
	public ResponseDataTable<User> searchUser(SearchDataTable<User> data, Authentication authentication);
	public List<User> getUserByRole(String role);
	public void changePassword(Authentication authentication, String newPassword);
	public void updateUserProfile(User user, Authentication authentication);
}
