package com.rest.mongo.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("users")
public class User {

	@Id
	private String id;
	private String email;
	private List<String> roles;
	private boolean isActive;
	private UserInfo userInfo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", roles=" + roles + ", isActive=" + isActive + ", userInfo="
				+ userInfo + "]";
	}

	public static User updateUser(User actualData, User newData) {
		if (newData.getEmail() != null) {
			actualData.setEmail(newData.getEmail());
		}
		
		if (newData.getRoles() != null) {
			actualData.setRoles(newData.getRoles());
		}
		
		if (newData.getIsActive() != actualData.getIsActive()) {
			actualData.setIsActive(newData.getIsActive());
		}
		
		if (newData.getUserInfo() != null) {
			if (actualData.getUserInfo() == null) {
				actualData.setUserInfo(new UserInfo());
			}
			actualData.setUserInfo(UserInfo.updateUserInfo(actualData.getUserInfo(), newData.getUserInfo()));
		}

		return actualData;
	}

}
