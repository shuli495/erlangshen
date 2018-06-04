package com.erlangshen.sdk.result.model;

import com.erlangshen.sdk.result.Result;

public class CertificationDetail extends Result {

    private Certification data;

    public Certification getData() {
        return data;
    }

    public void setData(Certification data) {
        this.data = data;
    }
}
