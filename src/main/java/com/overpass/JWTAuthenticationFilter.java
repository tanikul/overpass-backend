package com.overpass;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JWTAuthenticationFilter extends GenericFilterBean {
    
	@Value("${allowedOrigins}")
	private String[] allowedOrigins;
	
   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
           throws IOException, ServletException, ExpiredJwtException {
        
	   HttpServletResponse response = (HttpServletResponse) servletResponse;
       HttpServletRequest request= (HttpServletRequest) servletRequest;
       
       String origin = request.getHeader("Origin");
       List<String> arr = Arrays.asList(allowedOrigins);
       response.setHeader("Access-Control-Allow-Origin", (arr.contains(origin)) ? origin : "");
       response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
       response.setHeader("Access-Control-Allow-Headers", "*");
       response.setHeader("Access-Control-Allow-Credentials", "true");
       response.setHeader("Access-Control-Max-Age", "180");
    
        if(!request.getRequestURI().startsWith("/websocket")) {
        	
        	Authentication authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) servletRequest);
	        if(Objects.isNull(authentication)) {
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }else {
	        	SecurityContextHolder.getContext().setAuthentication(authentication);
	        }
           
        }
        filterChain.doFilter(servletRequest, response);
   }
}
