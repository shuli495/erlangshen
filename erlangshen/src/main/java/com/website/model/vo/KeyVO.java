package com.website.model.vo;

import com.website.model.bo.KeyBO;

public class KeyVO extends KeyBO {
	private static final long serialVersionUID = 1L;

	private String clientName;
	private String createdBy;

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
}