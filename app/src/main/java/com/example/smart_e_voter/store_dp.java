package com.example.smart_e_voter;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class store_dp {

    private String name, phone, address, age, mykey;
    Boolean permit;

    public String getMykey() {
        return mykey;
    }

    public void setMykey(String mykey) {
        this.mykey = mykey;
    }

    public store_dp() {
    }

    public store_dp(String name, String phone, String address, String age, Boolean permit) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.permit = permit;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Boolean getPermit() {
        return permit;
    }

    public String getAddress() {
        return address;
    }

    public String getAge() {
        return age;
    }
}
