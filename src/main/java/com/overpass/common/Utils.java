package com.overpass.common;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;

public class Utils {

	public static String generateRandomPassword(int len) {
        return RandomStringUtils.randomAlphanumeric(len);
    }
	
	public static String getRole(Authentication authentication)
	{
		Map<String, String> role = new HashMap<>();
		authentication.getAuthorities().forEach(roles -> {
			role.put("role", roles.getAuthority());
    	});
		return role.get("role");
		
	}
 
}
