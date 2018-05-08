package com.website.model.bo;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fastjavaframework.base.BaseBean;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class PermissionMenuBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotNull(message="{permission_menu.clientId.null}")
	@Size(max=64, min=0, message="{permission_menu.clientId.size}")
	private String clientId;

	private Integer parentId;

	// 0菜单 1功能
	@Max(value=1, message="{permission_menu.type.max}")
	private Integer type;

	// 菜单名称
	@Size(max=255, min=0, message="{permission_menu.name.size}")
	private String name;

	// 菜单url
	@Size(max=255, min=0, message="{permission_menu.url.size}")
	private String url;

	// 标签
	private String tag;


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

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}