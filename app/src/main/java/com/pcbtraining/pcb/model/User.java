package com.pcbtraining.pcb.model;

public class User {
    private String name;
    private String number;
    private String access;
    private String uid;
    private String email;

    public User(){}


    public User(String name, String number, String access, String uid, String email) {
        this.name = name;
        this.number = number;
        this.access = access;
        this.uid = uid;
        this.email = email;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
