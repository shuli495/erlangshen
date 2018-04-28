package com.website.model.vo;

import com.website.model.bo.UserBO;

public class UserVO extends UserBO {
	private static final long serialVersionUID = 1L;

	private String createdBy;	// client创建人

    private String clientName;  // 客户端名称

    private String code;        // 验证码

    private String oldPwd;      // 新密码

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