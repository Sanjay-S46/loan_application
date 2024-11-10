package com.loanapp.actions.verifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;

public class ProfileAction extends ActionSupport {
    
    private User user;
    private DatabaseConnection db = new DatabaseConnection();

    //setter 
    public void setUser(User user){
        this.user = user;
    }

    //getter
    public User getUser(){
        return user;
    }

    private void getUserInfo(String username){
        String query = "select emailId,mobileNo,gender,userType from users where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setUsername(username);
                user.setEmailId(resultSet.getString(1));
                user.setMobileNo(resultSet.getString(2));
                user.setGender(resultSet.getString(3));
                user.setUserType(resultSet.getString(4));
            }
            else{
                System.out.println("User details not available");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String execute(){
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        if (session != null) {
            User sessionUser = (User) session.getAttribute("userSession");
            if (sessionUser!=null && sessionUser.getUsername() != null) {
                getUserInfo(sessionUser.getUsername());
                return "success";
            }
            else{
                System.out.println("Username not available");
            }
        }
        return "error";
    }
}
