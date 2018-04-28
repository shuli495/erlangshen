package com.website.model.vo;

import com.website.model.bo.TokenBO;

public class TokenVO extends TokenBO {
	private static final long serialVersionUID = 1L;

	private String clientId;
	private String from;

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
}