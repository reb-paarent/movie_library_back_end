package com.example.movies.domain;

import jakarta.validation.constraints.*;

public class SignupForm {
	@NotBlank
	@Size(min = 5, max = 30)
	private String username = "";

	@NotBlank
	@Email
	@Size(max = 254)
	private String email = "";

	@NotBlank
	@Size(min = 12, max = 30, message = "Password must be between 12 and 30 characters.")
	private String password = "";

	@NotBlank
	@Size(min = 12, max = 30)
	private String passwordCheck = "";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordCheck() {
		return passwordCheck;
	}

	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}
}
