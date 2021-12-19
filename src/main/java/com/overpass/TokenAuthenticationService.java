package com.overpass;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.overpass.common.CustomUserDetails;
import com.overpass.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class TokenAuthenticationService {

	static final int EXPIRATIONTIME = 864000000; // 10 days
    
    static final String SECRET = "ThisIsASecret";
     
    static final String TOKEN_PREFIX = "Bearer";
     
    static final String HEADER_STRING = "Authorization";
    
    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);
    
 
    public static void addAuthentication(HttpServletResponse res, Authentication auth) throws IOException {

    	JSONObject payload = new JSONObject();
    	auth.getAuthorities().forEach(roles -> {
    		payload.put("role", roles.getAuthority());
    	});
    	CustomUserDetails customUserDetails = (CustomUserDetails)auth.getPrincipal();
    	User user = customUserDetails.getUser();
    	String JWT = Jwts.builder().setSubject(auth.getName())
    			.claim("role", payload.get("role"))
    			.claim("overpassGroup", user.getGroupId())
    			.claim("name", user.getFirstName() + " " + user.getLastName())
    			.claim("imageProfile", user.getImage())
                //.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    	JSONObject result = new JSONObject();
    	
    	result.put("access_token", JWT);
    	//result.put("access_token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJTVVBFUl9BRE1JTiIsIm92ZXJwYXNzR3JvdXAiOjI2LCJuYW1lIjoiQmVtcyBFbnRlcnByaXNlIiwiZXhwIjoxNjM3Mjg2NTg0fQ.cnVKXbG8QG_UrxzXElRp32iVwWBUthupogiswZOyTUP-zWL9fv89HzvIhDdFXP-dIFr-KCVlbuiep6WxUkCNDA");
    	result.put("refresh_token", doGenerateRefreshToken(auth.getName()));
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(result.toString());
    }
 
    public static String doGenerateRefreshToken(String subject) {

		return Jwts.builder().setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + (EXPIRATIONTIME * 4)))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

	}
    
    public static Authentication getAuthentication(HttpServletRequest request) {
    	try {
	        String token = request.getHeader(HEADER_STRING);
	        if (token != null) {
	            // parse the token.
	            String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
	                    .getSubject();
	            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
	            grantedAuthorities.add(new SimpleGrantedAuthority(Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody().get("role").toString()));
	            return user != null ? new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities) : null;
	        }
    	}catch (SignatureException e) {
    		logger.error("signature exception"+e);
    		return null;
        } catch (MalformedJwtException e) {
           logger.error("token malformed"+e);
           return null;
        } catch (ExpiredJwtException e) {
           logger.error("token expired"+e);
           return null;
        } catch (UnsupportedJwtException e) {
            logger.error("unsupported"+e);
            return null;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal"+e);
            return null;
        }
        return null;
    }
    
}
