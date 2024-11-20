package com.pcbtraining.pcb.model;

public class User {
    private String name;
    private String number;
    private String access;
    private String uid;
    private String email;

    private String access2;
    private String waccess;
    private String raccess;

    public User(){}


    public User(String name, String number, String access, String uid, String email,String access2,String waccess,String raccess) {
        this.name = name;
        this.number = number;
        this.access = access;
        this.uid = uid;
        this.email = email;
        this.access2 = access2;
        this.waccess = waccess;
        this.raccess = raccess;

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

    public String getAccess2() {
        return access2;
    }

    public void setAccess2(String access2) {
        this.access2 = access2;
    }

    public String getWaccess() {
        return waccess;
    }

    public void setWaccess(String waccess) {
        this.waccess = waccess;
    }

    public String getRaccess() {
        return raccess;
    }

    public void setRaccess(String raccess) {
        this.raccess = raccess;
    }
}
