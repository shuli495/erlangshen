package com.website.model.bo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fastjavaframework.base.BaseBean;

public class PermissionRoleBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotNull(message="{permission_role.clientId.null}")
	@Size(max=64, min=0, message="{permission_role.clientId.size}")
	private String clientId;

	// 角色
	@NotNull(message="{permission_role.role.null}")
	@Size(max=255, min=0, message="{permission_role.role.size}")
	private String role;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}