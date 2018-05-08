package com.website.model.bo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Max;

import com.fastjavaframework.base.BaseBean;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class PermissionRoleMenuBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotNull(message="{permission_role_function.roleId.null}")
	@Max(value=99999999, message="{permission_role_function.roleId.max}")
	private Integer roleId;

	@NotNull(message="{permission_role_menu.menuId.null}")
	@Max(value=99999999, message="{permission_role_menu.menuId.max}")
	private Integer menuId;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
}