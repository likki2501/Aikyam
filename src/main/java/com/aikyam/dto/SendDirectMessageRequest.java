package com.aikyam.dto;

public class SendDirectMessageRequest {
	private String senderMatrixUserId;
	private String senderUsername;
	private String senderPassword;
	private Integer receiverUserId;
	private String message;
	public String getSenderMatrixUserId() {
		return senderMatrixUserId;
	}
	public void setSenderMatrixUserId(String senderMatrixUserId) {
		this.senderMatrixUserId = senderMatrixUserId;
	}
	public String getSenderUsername() {
		return senderUsername;
	}
	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}
	public String getSenderPassword() {
		return senderPassword;
	}
	public void setSenderPassword(String senderPassword) {
		this.senderPassword = senderPassword;
	}
	
	public Integer getReceiverUserId() {
		return receiverUserId;
	}
	public void setReceiverUserId(Integer receiverUserId) {
		this.receiverUserId = receiverUserId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
