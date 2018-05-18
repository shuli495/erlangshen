package com.website.model.bo;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import com.fastjavaframework.base.BaseBean;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class UserBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private String id;	

	@Size(max=64, min=0, message="{user.clientGroup.size}")
	private String clientId;	//客户端id

	@NotNull(message="{user.pwd.null}")
	@Size(max=255, min=0, message="{user.pwd.size}")
	private String pwd;	//密码

	@Size(max=32, min=0, message="{user.username.size}")
	private String username;	//用户名

	@Size(max=32, min=0, message="{user.mail.size}")
	private String mail;	//邮箱

	@Size(max=32, min=0, message="{user.phone.size}")
	private String phone;	//手机号码

	@NotNull(message="{user.status.null}")
	private Integer status;	//状态 0正常 1邮箱手机未验证 2邮箱未验证 3手机未验证


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
