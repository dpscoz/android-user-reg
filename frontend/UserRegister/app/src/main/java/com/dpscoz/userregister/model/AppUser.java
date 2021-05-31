package com.dpscoz.userregister.model;

import java.util.Date;

public class AppUser {
    private Integer id;
    private String name;
    private Date birthday;
    private String picture;

    public AppUser() {
    }

    public AppUser(Integer id, String name, Date birthday, String picture) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.picture = picture;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
