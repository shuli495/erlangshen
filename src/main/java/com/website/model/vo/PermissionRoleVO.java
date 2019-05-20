package com.website.model.vo;

import com.alibaba.fastjson.JSONArray;
import com.website.model.bo.PermissionRoleBO;

import java.util.List;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class PermissionRoleVO extends PermissionRoleBO {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量关联角色用
	 */
	List<Integer> roles;

	List<String> users;

	List<Integer> menus;

	List<PermissionRoleMenuVO> menuInfo;

	public List<Integer> getRoles() {
		return roles;
	}

	public void setRoles(List<Integer> roles) {
		this.roles = roles;
	}

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

	public List<PermissionRoleMenuVO> getMenuInfo() {
		return menuInfo;
	}

	public void setMenuInfo(List<PermissionRoleMenuVO> menuInfo) {
		this.menuInfo = menuInfo;
	}

	@Override
	public String toString() {
		return super.toString() + " PermissionRoleVO{\"roles\": \""+ JSONArray.toJSONString(roles) +"\", "
				+ "\"users\": \""+JSONArray.toJSONString(users)+"\", "
				+ "\"menus\": \""+JSONArray.toJSONString(menus)+"\", "
				+ "\"menuInfo\": \""+JSONArray.toJSONString(menuInfo)+"\"}";
	}
}