package com.erlangshen.model.bo;

import com.fastjavaframework.base.BaseBean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class ClientPhoneBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	/**
	 * client key
	 */
	private Integer id;

	/**
	 * client名称
	 */
	@NotNull(message="{client_phone.clientId.null}")
	@Size(max=64, min=0, message="{client_phone.clientId.size}")
	private String clientId;

	/**
	 * 短信平台
	 */
	@Size(max=64, min=0, message="{client_phone.platform.size}")
	private String platform;

	@Size(max=64, min=0, message="{client_phone.ak.size}")
	private String ak;

	/**
	 * 用户注册发送邮件密码
	 */
	@Size(max=64, min=0, message="{client_phone.sk.size}")
	private String sk;

	/**
	 * 签名
	 */
	@Size(max=64, min=0, message="{client_phone.sign.size}")
	private String sign;

	/**
	 * 模板
	 */
	@Size(max=64, min=0, message="{client_phone.tmplate.size}")
	private String tmplate;

	/**
	 * 类型
	 */
	@Size(max=10, min=0, message="{client_phone.type.size}")
	private String type;

	/**
	 * 短信内容
	 */
	@Size(max=70, min=0, message="{client_phone.text.size}")
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

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getAk() {
		return ak;
	}

	public void setAk(String ak) {
		this.ak = ak;
	}

	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTmplate() {
		return tmplate;
	}

	public void setTmplate(String tmplate) {
		this.tmplate = tmplate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return super.toString() + " ClientPhoneBO{\"id\": \""+id+"\", \"clientId\": \""+clientId+"\", \"platform\": \""+platform+"\", "
				+ "\"ak\": \""+ak+"\", \"sk\": \""+sk+"\", \"sign\": \""+sign+"\", \"tmplate\": \""+tmplate+"\", "
				+ "\"type\": \""+type+"\", \"text\": \""+text+"\"}";
	}
}