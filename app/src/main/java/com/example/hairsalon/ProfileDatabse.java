package com.example.hairsalon;

public class ProfileDatabse {
    String name,mobile,email,address,dob;

    public ProfileDatabse() {

    }
    public ProfileDatabse(String name, String mobile, String email, String address, String dob) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
