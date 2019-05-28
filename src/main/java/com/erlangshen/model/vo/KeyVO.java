package com.erlangshen.model.vo;

import com.erlangshen.model.bo.KeyBO;

/**
 * @author https://github.com/shuli495/erlangshen
 */
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

	@Override
	public String toString() {
		return super.toString() + " KeyVO{\"clientName\": \""+clientName+"\", \"createdBy\": \""+createdBy+"\"}";
	}
}