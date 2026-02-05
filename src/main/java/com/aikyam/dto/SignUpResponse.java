package com.aikyam.dto;

public class SignUpResponse {
	public Integer userId;
	public String matrixUserId;
	
	public SignUpResponse(Integer userId,String matrixUserId) {
		this.userId = userId;
		this.matrixUserId = matrixUserId;
	}
}
