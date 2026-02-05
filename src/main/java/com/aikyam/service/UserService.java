package com.aikyam.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.aikyam.constants.Role;
import com.aikyam.dto.LoginRequest;
import com.aikyam.dto.MatrixLoginResponse;
import com.aikyam.dto.SignUpRequest;
import com.aikyam.dto.SignUpResponse;
import com.aikyam.model.User;
import com.aikyam.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final MatrixService matrixService;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository,
						MatrixService matrixService,
						PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.matrixService = matrixService;
		this.passwordEncoder = passwordEncoder;
	}
	
	public SignUpResponse signup(SignUpRequest request) {
		if(userRepository.existsByUsername(request.username)) {
			throw new RuntimeException("Username Already exists");
		}
		String matrixUserId = matrixService.createMatrixUser(request.username,request.password);
		User user = new User();
		user.setUsername(request.username);
		user.setEmail(request.email);
		user.setPasswordHash(passwordEncoder.encode(request.password));
		user.setMatrixUserId(matrixUserId);
		user.setRole(Role.DEVOTEE);
		user.setCreatedAt(Instant.now());
		System.out.println("Request reached here");
		userRepository.save(user);
		return new SignUpResponse(user.getUserId(), matrixUserId);
	}
	
	public MatrixLoginResponse login(@RequestBody LoginRequest request) throws JsonProcessingException {
		return matrixService.login(request);
	}
}
