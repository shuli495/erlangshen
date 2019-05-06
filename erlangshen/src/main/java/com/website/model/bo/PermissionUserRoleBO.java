package com.website.model.bo;

import com.fastjavaframework.base.BaseBean;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class PermissionUserRoleBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotNull(message="{permission_user_role.userId.null}")
	@Size(max=64, min=0, message="{permission_user_role.userId.size}")
	private String userId;

	@NotNull(message="{permission_user_role.roleId.null}")
	@Max(value=99999999, message="{permission_user_role.roleId.max}")
	private Integer roleId;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}


	@Override
	public String toString() {
		return super.toString() + " PermissionUserRoleBO{\"id\": \""+id+"\", \"userId\": \""+userId+"\", \"roleId\": \""+roleId+"\"}";
	}

}