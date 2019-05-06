package com.website.model.vo;

import com.alibaba.fastjson.JSONArray;
import com.website.model.bo.PermissionMenuBO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class PermissionMenuVO extends PermissionMenuBO {
	private static final long serialVersionUID = 1L;

	private List<PermissionMenuVO> menus;

	public List<PermissionMenuVO> getMenus() {
		if(null == menus) {
			menus = new ArrayList<>();
		}
		return menus;
	}

	public void setMenus(List<PermissionMenuVO> menus) {
		this.menus = menus;
	}


	@Override
	public String toString() {
		return super.toString() + " PermissionMenuVO{\"menus\": \""+ JSONArray.toJSONString(menus) +"\"}";
	}
}