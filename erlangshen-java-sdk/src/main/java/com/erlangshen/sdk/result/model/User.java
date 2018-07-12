package com.erlangshen.sdk.result.model;

import java.util.Date;

public class User {

    // 登录成功后获得的token
    private Token token;

    private String id;

    //客户端id
    private String clientId;

    //密码
    private String pwd;

    //用户名
    private String username;

    //邮箱
    private String mail;

    //邮箱是否验证 0未验证 1已验证
    private Integer mailVerify;

    //手机号码
    private String phone;

    // 手机号码是否验证 0未验证 1已验证
    private Integer phoneVerify;

    private Integer status;

    // 来源
    private String source;

    // 昵称
    private String nickname;

    // 电话
    private String tel;

    // QQ
    private String qq;

    // 微信
    private String weixin;

    // 新浪微博
    private String weibo;

    // 姓名
    private String name;

    // 性别 0女 1男
    private Boolean sex;

    // 身份证号
    private String idcard;

    // 实名认证 0未实名 1认证中 2认证失败 3认证成功
    private Integer certification;

    // 实名认证失败原因
    private String certificationFailMsg;

    // 省
    private String province;

    // 市
    private String city;

    // 区
    private String area;

    // 地址
    private String address;

    // 创建时间
    private Date createdTime;

    // 创建成后后自动登录
    private Boolean autoLogin;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

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

    public Integer getMailVerify() {
        return mailVerify;
    }

    public void setMailVerify(Integer mailVerify) {
        this.mailVerify = mailVerify;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPhoneVerify() {
        return phoneVerify;
    }

    public void setPhoneVerify(Integer phoneVerify) {
        this.phoneVerify = phoneVerify;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Boolean getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(Boolean autoLogin) {
        this.autoLogin = autoLogin;
    }
}
