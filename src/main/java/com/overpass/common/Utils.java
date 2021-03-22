package com.overpass.common;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	
	public static String dateFormatToString(Date date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}
	
	public static Date convertTimestampToDate(Timestamp time) {
		return new Date(time.getTime());  
	}
	
	
	
	/*public static String shortenUrl(String longUrl)
	{
        @SuppressWarnings("unused")
        OAuthService oAuthService = new ServiceBuilder().provider(GoogleApi.class).apiKey("anonymous").apiSecret("anonymous")
                        .scope("https://www.googleapis.com/auth/urlshortener") .build();
        OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, "https://www.googleapis.com/urlshortener/v1/url");
        oAuthRequest.addHeader("Content-Type", "application/json");
        String json = "{\"longUrl\": \"http://"+longUrl+"/\"}";
        oAuthRequest.addPayload(json);
        Response response = oAuthRequest.send();
        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> responseMap = new GsonBuilder().create().fromJson(response.getBody(), typeOfMap);
        String st=responseMap.get("id");
        return st;
    }*/
 
}
