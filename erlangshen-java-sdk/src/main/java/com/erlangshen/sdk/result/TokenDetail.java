package com.erlangshen.sdk.result;

import com.erlangshen.sdk.result.model.Token;

public class TokenDetail extends Result {
    private String image;
    private Token data;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Token getData() {
        return data;
    }

    public void setData(Token data) {
        this.data = data;
    }
}
