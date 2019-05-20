package com.website.model.vo;

import com.website.model.bo.TokenBO;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class TokenVO extends TokenBO {
	private static final long serialVersionUID = 1L;

	private String clientId;
	private String from;

	/**
	 * 认证方式AK/SK、TOKEN
	 */
	private String authenticationMethod;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAuthenticationMethod() {
		return authenticationMethod;
	}

	public void setAuthenticationMethod(String authenticationMethod) {
		this.authenticationMethod = authenticationMethod;
	}


	@Override
	public String toString() {
		return super.toString() + " TokenVO{\"clientId\": \""+clientId+"\", \"from\": \""+from+"\", "
				+ "\"authenticationMethod\": \""+authenticationMethod+"\"}";
	}
}