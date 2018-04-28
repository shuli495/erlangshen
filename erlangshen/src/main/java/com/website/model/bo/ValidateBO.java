package com.website.model.bo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

import com.fastjavaframework.base.BaseBean;

public class ValidateBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	@NotNull(message="{validate.userId.null}")
	@Size(max=64, min=0, message="{validate.userId.size}")
	private String userId;	

	@NotNull(message="{validate.type.null}")
	@Size(max=10, min=0, message="{validate.type.size}")
	private String type;	//类型 email phone

	@Size(max=255, min=0, message="{validate.code.size}")
	private String code;	

	private Date createdTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

}