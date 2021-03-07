package com.overpass;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JWTAuthenticationFilter extends GenericFilterBean {
    
   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
           throws IOException, ServletException {
        
	   HttpServletResponse response = (HttpServletResponse) servletResponse;
       HttpServletRequest request= (HttpServletRequest) servletRequest;

       response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
       response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
       response.setHeader("Access-Control-Allow-Headers", "*");
       response.setHeader("Access-Control-Allow-Credentials", "true");
       response.setHeader("Access-Control-Max-Age", "180");
       String xx = request.getRequestURI();
        if(!request.getRequestURI().startsWith("/websocket")) {
        	Authentication authentication = TokenAuthenticationService
                    .getAuthentication((HttpServletRequest) servletRequest);
             
            SecurityContextHolder.getContext().setAuthentication(authentication);
             
           
        }
        filterChain.doFilter(servletRequest, servletResponse);
   }
}
