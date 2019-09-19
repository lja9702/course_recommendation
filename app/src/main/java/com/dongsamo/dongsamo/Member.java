package com.dongsamo.dongsamo;

public class Member {
    private String id;
    private String email;
    private String name;
    private int korea, china, japan, america;

    public Member() {

    }

    public Member(String id, String email, String name, int korea, int china, int japan, int america) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.korea = korea;
        this.china = china;
        this.japan = japan;
        this.america = america;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKorea() {
        return korea;
    }

    public void setKorea(int korea) {
        this.korea = korea;
    }

    public int getChina() {
        return china;
    }

    public void setChina(int china) {
        this.china = china;
    }

    public int getJapan() {
        return japan;
    }

    public void setJapan(int japan) {
        this.japan = japan;
    }

    public int getAmerica() {
        return america;
    }

    public void setAmerica(int america) {
        this.america = america;
    }
}
