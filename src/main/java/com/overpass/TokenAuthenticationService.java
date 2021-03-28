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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.overpass.common.CustomUserDetails;
import com.overpass.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {

	static final long EXPIRATIONTIME = 864_000_000; // 10 days
    
    static final String SECRET = "ThisIsASecret";
     
    static final String TOKEN_PREFIX = "Bearer";
     
    static final String HEADER_STRING = "Authorization";
 
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
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    	JSONObject result = new JSONObject();
    	
    	result.put("access_token", JWT);
        //res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(result.toString());
    }
 
    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
                    .getSubject();
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            grantedAuthorities.add(new SimpleGrantedAuthority(Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody().get("role").toString()));
            return user != null ? new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities) : null;
        }
        return null;
    }
}
