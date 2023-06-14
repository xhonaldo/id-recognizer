package com.example.idrecognizer.common.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class IdInfo implements Serializable {
    private IdType idType;
    private String raw;
    private String name;
    private String lastname;
    private String type;
    private String gender;
    private String personalNo;
    private String birthdate;
    private transient Bitmap front;
    private transient Bitmap back;

    private String expiryDate;

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public boolean isCompleted(){
        return (name != null && lastname != null && gender != null && personalNo != null && birthdate != null && expiryDate != null);
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && name.length() > 1) {
            this.name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        if (lastname != null && lastname.length() > 1) {
            this.lastname = lastname.substring(0, 1).toUpperCase() + lastname.substring(1).toLowerCase();
        }
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonalNo() {
        return personalNo;
    }

    public void setPersonalNo(String personalNo) {
        this.personalNo = personalNo;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Bitmap getFront() {
        return front;
    }

    public void setFront(Bitmap front) {
        this.front = front;
    }

    public Bitmap getBack() {
        return back;
    }

    public void setBack(Bitmap back) {
        this.back = back;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
