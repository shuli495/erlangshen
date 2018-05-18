package com.website.model.bo;

import javax.validation.constraints.Size;
import javax.validation.constraints.Max;
import java.util.Date;
import javax.validation.constraints.NotNull;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class UserInfoBO extends UserBO {
	private static final long serialVersionUID = 1L;

	private String id;

	// 来源
	@Size(max=255, min=0, message="{user_info.source.size}")
	private String source;

	// 昵称
	@Size(max=8, min=0, message="{user_info.nickname.size}")
	private String nickname;

	// 电话
	@Size(max=32, min=0, message="{user_info.tel.size}")
	private String tel;

	// QQ
	@Size(max=32, min=0, message="{user_info.qq.size}")
	private String qq;

	// 微信
	@Size(max=32, min=0, message="{user_info.weixin.size}")
	private String weixin;

	// 新浪微博
	@Size(max=32, min=0, message="{user_info.weibo.size}")
	private String weibo;

	// 姓名
	@Size(max=32, min=0, message="{user_info.name.size}")
	private String name;

	// 性别 0女 1男
	private Boolean sex;

	// 身份证号
	@Size(max=18, min=0, message="{user_info.idcard.size}")
	private String idcard;

	// 实名认证 0未实名 1认证中 2认证失败 3认证成功
	@Max(value=99999999, message="{user_info.certification.max}")
	private Integer certification;

	// 实名认证失败原因
	@Size(max=255, min=0, message="{user_info.certificationFailMsg.size}")
	private String certificationFailMsg;

	// 省
	@Size(max=64, min=0, message="{user_info.province.size}")
	private String province;

	// 市
	@Size(max=64, min=0, message="{user_info.city.size}")
	private String city;

	// 区
	@Size(max=64, min=0, message="{user_info.area.size}")
	private String area;

	// 地址
	@Size(max=255, min=0, message="{user_info.address.size}")
	private String address;

	// 创建时间
	@NotNull(message="{user_info.createdTime.null}")
	private Date createdTime;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
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

}