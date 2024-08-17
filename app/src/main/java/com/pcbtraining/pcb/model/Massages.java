package com.pcbtraining.pcb.model;

public class Massages {
    private String name;
    private String msg;
    private String date;
    private String seen;
    private String id; // ( optional )
    private String uid; // (optional )


    public Massages(String name, String msg, String date, String seen, String id, String uid) {

        this.name = name;
        this.msg = msg;
        this.date = date;
        this.seen = seen;
        this.id = id;
        this.uid = uid;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
