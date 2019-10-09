package com.erlangshen.model.vo;

public class LoginVO {

    /**
     * 是否校验用户激活状态
     */
    private boolean isCheckStatus;

    private String userName;
    private String pwd;
    private String robotCode;
    private String platform;
    private String loginIp;
    private String token;

    public boolean isCheckStatus() {
        return isCheckStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        isCheckStatus = checkStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
