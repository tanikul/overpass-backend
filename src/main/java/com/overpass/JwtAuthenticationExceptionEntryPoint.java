package com.overpass;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.overpass.model.ReponseBody;

public class JwtAuthenticationExceptionEntryPoint  implements AuthenticationEntryPoint {

 
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

         ObjectMapper mapper = new ObjectMapper();
         ReponseBody rs = new ReponseBody();
         rs.setCode("9999");
         rs.setMessage("Login fail");
         response.setStatus(HttpStatus.UNAUTHORIZED.value());
         response.setContentType("application/json");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(mapper.writeValueAsString(rs));
    }

}
