package com.website.model.bo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

import com.fastjavaframework.base.BaseBean;

public class LoginLogBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private Integer id;

	@NotNull(message="{login_log.userId.null}")
	@Size(max=64, min=0, message="{login_log.userId.size}")
	private String userId;

	// 登录ip
	@Size(max=128, min=0, message="{login_log.ip.size}")
	private String ip;

	// 登录时间
	@NotNull(message="{login_log.loginTime.null}")
	private Date loginTime;


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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

}