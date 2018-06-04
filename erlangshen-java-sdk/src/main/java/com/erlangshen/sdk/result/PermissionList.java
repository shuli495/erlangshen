package com.erlangshen.sdk.result;

import com.erlangshen.sdk.result.model.Permission;

public class PermissionList extends Result {

    private Permission data;

    public Permission getData() {
        return data;
    }

    public void setData(Permission data) {
        this.data = data;
    }
}
