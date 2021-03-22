package com.overpass.reposiroty;

import java.util.List;

import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;

public interface UserRepository {

	public User getUserById(int id);
	public User checkLogin(String username);
	public void inserUser(User user);
	public void updateUser(User user);
	public void deleteUser(int id);
	public ResponseDataTable<User> searchUser(SearchDataTable<User> data, String role, String createBy);
	public int countByUsername(String username);
	public User getUserByUsername(String username);
	public List<User> getUserByRole(String role);
	public void changePassword(int id, String newPassword);
}
