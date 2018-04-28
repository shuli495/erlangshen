package com.website.model.vo;

import com.website.model.bo.PermissionRoleBO;

import java.util.List;

public class PermissionRoleVO extends PermissionRoleBO {
	private static final long serialVersionUID = 1L;

	List<String> users;

	List<Integer> menus;

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<Integer> getMenus() {
		return menus;
	}

	public void setMenus(List<Integer> menus) {
		this.menus = menus;
	}
}