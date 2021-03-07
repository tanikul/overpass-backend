package com.overpass.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.overpass.common.Constants;
import com.overpass.common.Constants.Status;
import com.overpass.common.Utils;
import com.overpass.model.ResponseDataTable;
import com.overpass.model.SearchDataTable;
import com.overpass.model.User;
import com.overpass.reposiroty.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public User getUserById(int id) {
		return userRepository.getUserById(id);
	}

	@Override
	public void inserUser(User user, Authentication authentication) throws Exception {
		// Fix //
		String password  = passwordEncoder.encode("1234");
		user.setStatus(Status.ACTIVE);
		user.setPassword(password);
		User u = userRepository.getUserByUsername(authentication.getName());
		user.setCreateBy(u.getId());
		Utils.getRole(authentication);
		if(userRepository.countByUsername(user.getUsername()) == 0) {
			userRepository.inserUser(user);
		}else {
			throw new Exception("username duplicate.");
		}
	}

	@Override
	public void updateUser(User user, Authentication authentication) {
		User u = userRepository.getUserByUsername(authentication.getName());
		Utils.getRole(authentication);
		user.setCreateBy(u.getId());
		userRepository.updateUser(user);
		
	}

	@Override
	public void deleteUser(int id) {
		
		userRepository.deleteUser(id);
		
	}

	@Override
	public ResponseDataTable<User> searchUser(SearchDataTable<User> data, Authentication authentication) {
		return userRepository.searchUser(data, Utils.getRole(authentication), authentication.getName());
	}

	@Override
	public List<User> getUserByRole(String role) {
		return userRepository.getUserByRole(role);
	}

}
