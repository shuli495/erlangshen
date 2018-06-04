package com.erlangshen.sdk.result.model;

public class Certification {

    private String name;
    private String idcard;
    private Integer certification;
    private String certificationFailMsg;

    private String font;
    private String back;
    private String holdFont;
    private String holdBack;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getHoldFont() {
        return holdFont;
    }

    public void setHoldFont(String holdFont) {
        this.holdFont = holdFont;
    }

    public String getHoldBack() {
        return holdBack;
    }

    public void setHoldBack(String holdBack) {
        this.holdBack = holdBack;
    }
}
