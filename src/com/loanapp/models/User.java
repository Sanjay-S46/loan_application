package com.loanapp.models;

public class User {
    
    private String username;
    private String emailId;
    private String mobileNo;
    private String gender;
    private String userType;
    private String password;

    //setters
    public void setUsername(String username){
        this.username = username;
    }
    public void setEmailId(String emailId){
        this.emailId = emailId;
    }
    public void setMobileNo(String mobileNo){
        this.mobileNo = mobileNo;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    public void setUserType(String userType){
        this.userType = userType;
    }
    public void setPassword(String password){
        this.password = password;
    }

    //getters
    public String getUsername(){
        return username;
    }
    public String getEmailId(){
        return emailId;
    }
    public String getMobileNo(){
        return mobileNo;
    }
    public String getGender(){
        return gender;
    }
    public String getUserType(){
        return userType;
    }
    public String getPassword(){
        return password;
    }
}
