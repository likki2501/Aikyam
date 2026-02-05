package com.aikyam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aikyam.dto.LoginRequest;
import com.aikyam.dto.MatrixLoginResponse;
import com.aikyam.dto.MatrixTokenCache;

@Service
public class AuthService {
	@Autowired
	private MatrixTokenCache tokenCache;
	@Autowired
	private MatrixService matrixService;
	
	public void ensureSession(
			String matrixUserId,
			String username,
			String password) {
		if(tokenCache.hasSession(matrixUserId)) {
			return;
		}
		LoginRequest request = new LoginRequest();
		request.setUsername(username);
		request.setPassword(password);
		MatrixLoginResponse response = matrixService.login(request);
		
		tokenCache.storeToken(response.getUser_id(), response.getAccess_token());
	}
}
