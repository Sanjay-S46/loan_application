package com.loanapp.models;

public class User {
    
    private int userId;
    private String username;
    private String emailId;
    private String mobileNo;
    private String gender;
    private String userType;
    private String password;
    private String status;

    //setters
    public void setUserId(int userId){
        this.userId = userId;
    }
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
    public void setStatus(String status){
        this.status = status;
    }

    //getters
    public int getUserId(){
        return userId;
    }
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
    public String getStatus(){
        return status;
    }
}
