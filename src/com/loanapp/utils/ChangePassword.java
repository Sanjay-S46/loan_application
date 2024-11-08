package com.loanapp.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;

public class ChangePassword {

    private DatabaseConnection db = new DatabaseConnection();
    
    private String currentPassword;
    private String newPassword;
    private HttpSession session = ServletActionContext.getRequest().getSession(false);
    private User user = (User) session.getAttribute("userSession");
    private String salt;
    private byte[] byteSalt;

    //setters
    public void setNewPassword(String newPassword){
        this.newPassword = newPassword;
    }
    public void setCurrentPassword(String currentPassword){
        this.currentPassword = currentPassword;
    }

    //getters
    public String getNewPassword(){
        return newPassword;
    }
    public String getCurrentPassword(){
        return currentPassword;
    }

    //check if the current password is correct or not
    private boolean isCorrectPassword(){
        String query = "select password,randomSalt from user where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                
                String pwd = resultSet.getString("password");
                salt = resultSet.getString("randomSalt");
                byteSalt = Base64.getDecoder().decode(salt);

                
                return HashPassword.verifyPassword(getCurrentPassword(), pwd, salt);
            }
            else{
                return false;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // method for changing the new password
    public boolean changeUserPassword(){

        if (!isCorrectPassword()) {
            return false;
        }

        try {
            setNewPassword(HashPassword.hashPassword(getNewPassword(), byteSalt));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        String query = "update user set password=? where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, getNewPassword());
            preparedStatement.setString(2, user.getUsername());

            int value = preparedStatement.executeUpdate();
            return value > 0;
        } 
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public String execute(){
        if (changeUserPassword()) {
            System.out.println("password changed successfully..");
            return "success";
        }
        else{
            return "error";
        }
    }
}
