package com.overpass.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
	@Autowired
    private FileStorageService fileStorageService;
	
	@Override
	public User getUserById(int id) {
		return userRepository.getUserById(id);
	}

	@Override
	public void inserUser(User user, MultipartFile imageProfile, Authentication authentication) throws Exception {
		String passwordTmp = user.getUsername();
		String password  = passwordEncoder.encode(user.getUsername());
		user.setStatus(Status.ACTIVE);
		user.setPassword(password);
		User u = userRepository.getUserByUsername(authentication.getName());
		user.setCreateBy(u.getId());
		Utils.getRole(authentication);
		if(userRepository.countByUsername(user.getUsername()) == 0) {
			if(imageProfile != null) {
				Optional<String> ext = getExtensionByStringHandling(imageProfile.getOriginalFilename());
				UUID uuid = UUID.randomUUID();
				String fileName = uuid.toString() + "." + ext.get();
				fileStorageService.storeFile(imageProfile, fileName);
				user.setImage(fileName);
			}
			userRepository.inserUser(user);
			user.setPassword(passwordTmp);
			senEmail(user);
		}else {
			throw new Exception("username duplicate.");
		}
	}

	private Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
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
		String tmpPassword = newPassword;
		newPassword  = passwordEncoder.encode(newPassword);
		userRepository.changePassword(u.getId(), newPassword);
		u.setPassword(tmpPassword);
		senEmailChangePassword(u);
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
	
	private void senEmailChangePassword(User user) {
		try {
			String subject = "แจ้งการเปลี่ยนพาสเวิร์ดกับระบบ Smart Light Bangkok";
			String body = "เรียนคุณ " + user.getFirstName() + " " + user.getLastName();
			body += "\n\n              ";
			body += "ระบบได้ทำการเปลี่ยนพาสเวิร์ดให้คุณแล้ว โดยใช้";
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
