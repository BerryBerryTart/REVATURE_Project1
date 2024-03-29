package com.berry.DTO;

public class LoginDTO implements DTO{
	private String username;
	private String password;
	
	public LoginDTO() {
		super();
	}
		
	public LoginDTO(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public boolean noFieldEmpty() {
		if (this.username == null || this.username.trim().equals("") == true) {
			return false;
		} else if (this.password == null || this.password.trim().equals("") == true) {
			return false;
		} 
		
		return true;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginDTO other = (LoginDTO) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}	
}
