package com.loanapp.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.loanapp.utils.HashPassword;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class LoginAction extends ActionSupport implements ModelDriven<User>{

    private DatabaseConnection db = new DatabaseConnection();
    private User user = new User();

    private boolean isAdminUser = false;
    private boolean isVerifier = false;

    @Override
    public User getModel() {
        return user;
    }

    // authenticating the user
    private boolean checkUser(){
        String query = "select randomSalt,password,userType from users where username=?" ;
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next()){

                String salt = resultSet.getString("randomSalt");
                String pwd = resultSet.getString("password");
                String userType = resultSet.getString("userType");

                if (userType.equals("admin")) {
                    isAdminUser = true;
                }
                else if (userType.equals("verifier")) {
                    isVerifier = true;
                }
                
                user.setUserType(userType);
                
                return HashPassword.verifyPassword(user.getPassword(), pwd, salt);
            }
            else{
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String execute(){
        
        if (checkUser()) {
            HttpSession session = ServletActionContext.getRequest().getSession();
            session.setAttribute("userSession", user);
            if (isAdminUser) {
                return "admin_user";
            }
            else if (isVerifier) {
                return "verifier";
            }

            return "success";
        }
        return "error";
    }
    

}
