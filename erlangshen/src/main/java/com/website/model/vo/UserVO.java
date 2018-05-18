package com.website.model.vo;

import com.website.model.bo.UserInfoBO;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class UserVO extends UserInfoBO {
	private static final long serialVersionUID = 1L;

    // client创建人
	private String createdBy;

    // 客户端名称
    private String clientName;

    // 验证码
    private String code;

    // 新密码
    private String oldPwd;

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
}