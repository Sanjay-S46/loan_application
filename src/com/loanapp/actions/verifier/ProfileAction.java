package com.loanapp.actions.verifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class ProfileAction extends ActionSupport {
    
    private DatabaseConnection db = new DatabaseConnection();
    private User user;

    //getter 
    public User getUser() {
        return user;
    }

    //setter
    public void setUser(User user) {
        this.user = user;
    }

    private void getUserInfo(String username) {
        String query = "select * from user where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setUsername(username);
                user.setEmailId(resultSet.getString("emailId"));
                user.setMobileNo(resultSet.getString("mobileNo"));
                user.setGender(resultSet.getString("gender"));
                user.setUserType(resultSet.getString("userType"));
                setUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String execute() {
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        if (session != null) {
            User sessionUser = (User) session.getAttribute("userSession");
            if (sessionUser != null) {
                getUserInfo(sessionUser.getUsername());
                System.out.println("User info successfully set.");
                return SUCCESS;
            }
        }
        return Action.ERROR;
    }
}
