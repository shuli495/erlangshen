package com.erlangshen.model.vo;

import com.erlangshen.model.bo.UserInfoBO;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class UserVO extends UserInfoBO {
	private static final long serialVersionUID = 1L;

    /**
     * client创建人
     */
	private String createdBy;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 邮箱、手机验证码
     */
    private String code;

    /**
     * 防机器人校验验证码
     */
    private String robotCode;

    /**
     * 新密码
     */
    private String oldPwd;

    /**
     * 登录成功后获得的token
     */
    private TokenVO token;

    /**
     * 创建成功后获取token
     */
    private Boolean autoLogin;

    /**
     * 登录客户端ip
     */
    private String loginIp;

    /**
     * 登录客户端平台
     */
    private String platform;

    /**
     * 取消生成机器人验证码
     */
    private boolean calanceRobotCode;

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public TokenVO getToken() {
        return token;
    }

    public void setToken(TokenVO token) {
        this.token = token;
    }

    public Boolean getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(Boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean getCalanceRobotCode() {
        return calanceRobotCode;
    }

    public void setCalanceRobotCode(boolean calanceRobotCode) {
        this.calanceRobotCode = calanceRobotCode;
    }

    @Override
    public String toString() {
        return super.toString() + " UserVO{\"createdBy\": \""+createdBy+"\", \"clientName\": \""+clientName+"\", "
                + "\"code\": \""+code+"\", \"oldPwd\": \""+oldPwd+"\", \"token\": \""+token+"\", "
                + "\"autoLogin\": \""+autoLogin+"\", \"loginIp\": \""+loginIp+"\", \"platform\": \""+platform+"\","
                + "\"calanceRobotCode\": \"" + calanceRobotCode + "}";
    }
}