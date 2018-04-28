package com.website.model.bo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Max;
import java.util.Date;

import com.fastjavaframework.base.BaseBean;

public class KeyBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	@NotNull(message="{key.access.null}")
	@Size(max=255, min=0, message="{key.access.size}")
	private String access;

	@NotNull(message="{key.secret.null}")
	@Size(max=255, min=0, message="{key.secret.size}")
	private String secret;

	@NotNull(message="{key.clientId.null}")
	@Size(max=64, min=0, message="{key.clientId.size}")
	private String clientId;

	// 状态 1启用 0停用
	@NotNull(message="{key.status.null}")
	@Max(value=10, message="{key.status.max}")
	private Integer status;

	@NotNull(message="{key.createdTime.null}")
	private Date createdTime;


	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

}