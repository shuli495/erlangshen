package com.website.model.bo;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import java.util.Date;

import com.fastjavaframework.base.BaseBean;

public class UserRecycleBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	private String id;

	// 客户端
	@Size(max=64, min=0, message="{user_recycle.clientId.size}")
	private String clientId;

	// 来源
	@Size(max=255, min=0, message="{user_recycle.source.size}")
	private String source;

	// 密码
	@NotNull(message="{user_recycle.pwd.null}")
	@Size(max=255, min=0, message="{user_recycle.pwd.size}")
	private String pwd;

	// 用户名
	@Size(max=32, min=0, message="{user_recycle.username.size}")
	private String username;

	// 昵称
	@Size(max=8, min=0, message="{user_recycle.nickname.size}")
	private String nickname;

	// 邮箱
	@Size(max=32, min=0, message="{user_recycle.mail.size}")
	private String mail;

	// 手机号码
	@Size(max=32, min=0, message="{user_recycle.phone.size}")
	private String phone;

	// 电话
	@Size(max=32, min=0, message="{user_recycle.tel.size}")
	private String tel;

	// QQ
	@Size(max=32, min=0, message="{user_recycle.qq.size}")
	private String qq;

	// 微信
	@Size(max=32, min=0, message="{user_recycle.weixin.size}")
	private String weixin;

	// 微信二维码
	@Size(max=255, min=0, message="{user_recycle.wexinImg.size}")
	private String wexinImg;

	// 新浪微博
	@Size(max=32, min=0, message="{user_recycle.weibo.size}")
	private String weibo;

	// 头像
	@Size(max=255, min=0, message="{user_recycle.head.size}")
	private String head;

	// 姓名
	@Size(max=32, min=0, message="{user_recycle.name.size}")
	private String name;

	// 性别 0女 1男
	private Boolean sex;

	// 身份证号
	@Size(max=18, min=0, message="{user_recycle.idcard.size}")
	private String idcard;

	// 身份证图片
	@Size(max=255, min=0, message="{user_recycle.idcardImg.size}")
	private String idcardImg;

	// 省
	@Size(max=64, min=0, message="{user_recycle.province.size}")
	private String province;

	// 市
	@Size(max=64, min=0, message="{user_recycle.city.size}")
	private String city;

	// 区
	@Size(max=64, min=0, message="{user_recycle.area.size}")
	private String area;

	// 地址
	@Size(max=255, min=0, message="{user_recycle.address.size}")
	private String address;

	// 创建时间
	private Date createdTime;

	// 操作时间
	@NotNull(message="{user_recycle.operatorTime.null}")
	private Date operatorTime;

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

	public String getIdcardImg() {
		return idcardImg;
	}

	public void setIdcardImg(String idcardImg) {
		this.idcardImg = idcardImg;
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

	public Date getOperatorTime() {
		return operatorTime;
	}

	public void setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}