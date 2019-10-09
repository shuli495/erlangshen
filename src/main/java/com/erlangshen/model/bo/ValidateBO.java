package com.erlangshen.model.bo;

import com.fastjavaframework.base.BaseBean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class ValidateBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	@NotNull(message="{validate.userId.null}")
	@Size(max=65, min=0, message="{validate.userId.size}")
	private String userId;

	/**
	 * 类型 email phone
	 */
	@NotNull(message="{validate.type.null}")
	@Size(max=16, min=0, message="{validate.type.size}")
	private String type;

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

	@Override
	public String toString() {
		return super.toString() + " ValidateBO{\"userId\": \""+userId+"\", \"type\": \""+type+"\", "
				+ "\"code\": \""+code+"\", \"createdTime\": \""+createdTime+"\"}";
	}
}