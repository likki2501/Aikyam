package com.aikyam.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aikyam.dto.LoginRequest;
import com.aikyam.dto.MatrixLoginResponse;
import com.aikyam.dto.SignUpRequest;
import com.aikyam.dto.SignUpResponse;
import com.aikyam.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

@CrossOrigin(
	    originPatterns = "*",
	    allowedHeaders = "*",
	    methods = {RequestMethod.POST, RequestMethod.OPTIONS}
	)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final UserService userService;
	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/signup")
	public SignUpResponse signup(@RequestBody SignUpRequest request) {
		return userService.signup(request);
	}
	
	@PostMapping("/login")
	public MatrixLoginResponse login(@RequestBody LoginRequest request) throws JsonProcessingException {
		return userService.login(request);
	}
}
