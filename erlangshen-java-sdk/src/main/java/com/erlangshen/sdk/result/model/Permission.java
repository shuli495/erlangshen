package com.erlangshen.sdk.result.model;

import java.util.List;

public class Permission {

    private Integer id;

    private String role;

    private List<Menu> menuInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Menu> getMenuInfo() {
        return menuInfo;
    }

    public void setMenuInfo(List<Menu> menuInfo) {
        this.menuInfo = menuInfo;
    }
}
