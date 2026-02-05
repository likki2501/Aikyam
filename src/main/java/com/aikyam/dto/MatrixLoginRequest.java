package com.aikyam.dto;

public class MatrixLoginRequest {
	private String type = "m.login.password";
	private Identifier identifier;
	private String password;
	
	public static class Identifier{
		private String type = "m.id.user";
		private String user;
		public String getType() {
			return type;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
	}

	public String getType() {
		return type;
	}
	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
