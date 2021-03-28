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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
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
			senEmail(user);
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

	@Override
	public void changePassword(Authentication authentication, String newPassword) {
		User u = userRepository.getUserByUsername(authentication.getName());
		newPassword  = passwordEncoder.encode(newPassword);
		userRepository.changePassword(u.getId(), newPassword);
		
	}

	@Override
	public User getUser(Authentication authentication) {
		return userRepository.getUserByUsername(authentication.getName());
	}

	@Override
	public void updateUserProfile(User user, Authentication authentication) {
		User u = userRepository.getUserByUsername(authentication.getName());
		Utils.getRole(authentication);
		user.setCreateBy(u.getId());
		user.setId(u.getId());
		userRepository.updateUserProfile(user);
	}
	
	private void senEmail(User user) {
		try {
			String subject = "แจ้งการลงทะเบียนกับระบบ Smart Light Bangkok";
			String body = "เรียนคุณ " + user.getFirstName() + " " + user.getLastName();
			body += "\n\n              ";
			body += "ระบบได้ทำการลงทะเบียนให้คุณแล้ว โดยใช้";
			body += "\n                ";
			body += "username : " + user.getUsername();
			body += "\n                ";
			body += "password : " + user.getPassword();
			body += "\n\n";
			//body += "ขอบคุณครับ";
 			emailService.sendSimpleMessage(user.getEmail(), subject, body);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

}
