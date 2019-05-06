package com.website.model.bo;

import com.fastjavaframework.base.BaseBean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class ClientMailBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	/**
	 * client key
	 */
	private Integer id;

	/**
	 * client名称
	 */
	@NotNull(message="{client_mail.clientId.null}")
	@Size(max=64, min=0, message="{client_mail.clientId.size}")
	private String clientId;

	/**
	 * 用户注册发送邮件账号
	 */
	@Size(max=64, min=0, message="{client_mail.mail.size}")
	private String mail;

	@Size(max=64, min=0, message="{client_mail.username.size}")
	private String username;

	/**
	 * 用户注册发送邮件密码
	 */
	@Size(max=64, min=0, message="{client_mail.pwd.size}")
	private String pwd;

	@Size(max=64, min=0, message="{client_mail.smtp.size}")
	private String smtp;

	/**
	 * 注册邮件内容
	 */
	@Size(max=10, min=0, message="{client_mail.type.size}")
	private String type;

	/**
	 * 标题
	 */
	@Size(max=10, min=0, message="{client_mail.type.subject}")
	private String subject;

	/**
	 * 注册短信内容
	 */
	@Size(max=21000, min=0, message="{client_mail.text.size}")
	private String text;


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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return super.toString() + " ClientMailBO{\"id\": \""+id+"\", \"clientId\": \""+clientId+"\", \"mail\": \""+mail+"\", "
				+ "\"username\": \""+username+"\", \"pwd\": \""+pwd+"\", \"smtp\": \""+smtp+"\", \"type\": \""+type+"\", "
				+ "\"subject\": \""+subject+"\", \"text\": \""+text+"\"}";
	}

}