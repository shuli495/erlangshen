package com.erlangshen.sdk.result;

import com.erlangshen.sdk.result.model.User;

public class UserDetail extends Result {

    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
