package com.website.model.bo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

import com.fastjavaframework.base.BaseBean;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class TokenBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private String id;	

	@NotNull(message="{token.userId.null}")
	@Size(max=64, min=0, message="{token.userId.size}")
	private String userId;	

	@NotNull(message="{token.activeTime.null}")
	private Date activeTime;	//有效时间

	// 客户端ip
	@Size(max=128, min=0, message="{token.ip.size}")
	private String ip;

	private Date createdTime;

	// 登录端 用于区分在不同平台是否可以重复登录
	@Size(max=255, min=0, message="{token.client.size}")
	private String platform;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(Date activeTime) {
		this.activeTime = activeTime;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
