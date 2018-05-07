package com.website.model.bo;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import java.util.Date;

import com.fastjavaframework.base.BaseBean;

public class
UserBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private String id;	

	@Size(max=64, min=0, message="{user.clientGroup.size}")
	private String clientId;	//客户端id

	// 来源
	@Size(max=255, min=0, message="{user.source.size}")
	private String source;

	@NotNull(message="{user.pwd.null}")
	@Size(max=255, min=0, message="{user.pwd.size}")
	private String pwd;	//密码

	@Size(max=32, min=0, message="{user.username.size}")
	private String username;	//用户名

	@Size(max=8, min=0, message="{user.nickname.size}")
	private String nickname;	//昵称

	@Size(max=32, min=0, message="{user.mail.size}")
	private String mail;	//邮箱

	@Size(max=32, min=0, message="{user.phone.size}")
	private String phone;	//手机号码

	@Size(max=32, min=0, message="{user.tel.size}")
	private String tel;	//电话

	@Size(max=32, min=0, message="{user.qq.size}")
	private String qq;	//QQ

	@Size(max=32, min=0, message="{user.weixin.size}")
	private String weixin;	//微信

	@Size(max=255, min=0, message="{user.wexinImg.size}")
	private String wexinImg;	//微信二维码

	@Size(max=32, min=0, message="{user.weibo.size}")
	private String weibo;	//新浪微博

	@Size(max=255, min=0, message="{user.head.size}")
	private String head;	//头像

	@Size(max=32, min=0, message="{user.name.size}")
	private String name;	//姓名

	private Boolean sex;	//性别 0女 1男

	@Size(max=18, min=0, message="{user.idcard.size}")
	private String idcard;	//身份证号

	private Integer certification;	// 实名认证 0未实名 1认证中 2认证失败 3认证成功

	@Size(max=255)
	private String certificationFailMsg;	// 实名认证失败原因

	@Size(max=64, min=0, message="{user.province.size}")
	private String province;	//省

	@Size(max=64, min=0, message="{user.city.size}")
	private String city;	//市

	@Size(max=64, min=0, message="{user.area.size}")
	private String area;	//区

	@Size(max=255, min=0, message="{user.address.size}")
	private String address;	//地址

	private Date createdTime;	//创建时间

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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getWexinImg() {
		return wexinImg;
	}

	public void setWexinImg(String wexinImg) {
		this.wexinImg = wexinImg;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public Integer getCertification() {
		return certification;
	}

	public void setCertification(Integer certification) {
		this.certification = certification;
	}

	public String getCertificationFailMsg() {
		return certificationFailMsg;
	}

	public void setCertificationFailMsg(String certificationFailMsg) {
		this.certificationFailMsg = certificationFailMsg;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
