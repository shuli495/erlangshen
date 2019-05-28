package com.erlangshen.model.bo;

import com.fastjavaframework.base.BaseBean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class PermissionRoleBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotNull(message="{permission_role.clientId.null}")
	@Size(max=64, min=0, message="{permission_role.clientId.size}")
	private String clientId;

	/**
	 * 角色
	 */
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


	@Override
	public String toString() {
		return super.toString() + " PermissionRoleBO{\"id\": \""+id+"\", \"clientId\": \""+clientId+"\", \"role\": \""+role+"\"}";
	}
}