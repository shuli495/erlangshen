package com.website.model.vo;

import com.website.model.bo.PermissionMenuBO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class PermissionMenuVO extends PermissionMenuBO {
	private static final long serialVersionUID = 1L;

	private List<PermissionMenuVO> menus = new ArrayList<>();

	public List<PermissionMenuVO> getMenus() {
		return menus;
	}

	public void setMenus(List<PermissionMenuVO> menus) {
		this.menus = menus;
	}
}