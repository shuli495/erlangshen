package com.website.model.vo;

import com.website.model.bo.LoginLogBO;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class LoginLogVO extends LoginLogBO {
	private static final long serialVersionUID = 1L;

	private String username;
	private String nickname;
	private String mail;
	private String phone;
	private String clientId;
	private String clientName;
	private String createdBy;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	@Override
	public String toString() {
		return super.toString() + " LoginLogVO{\"username\": \""+username+"\", \"nickname\": \""+nickname+"\", "
				+ "\"mail\": \""+mail+"\", \"phone\": \""+phone+"\", \"clientId\": \""+clientId+"\", " +
				"\"clientName\": \""+clientName+"\", \"createdBy\": \""+createdBy+"\"}";
	}
}