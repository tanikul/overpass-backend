package com.overpass.controller;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.overpass.model.ErrorMessage;

import org.springframework.core.Ordered;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestResponseEntityExceptionHandler 
  extends ResponseEntityExceptionHandler {

	
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorMessage> handleAllExceptions(Exception ex, WebRequest request) {
    	ErrorMessage error = new ErrorMessage();
    	error.setCode("9999");
    	error.setMessage(ex.getMessage());

      return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}