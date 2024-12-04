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
    private boolean isLender = false;
    private boolean isBorrower = false;
    private static final int MAX_LOGIN_ATTEMPTS = 5;


    @Override
    public User getModel() {
        return user;
    }

    // method used for getting the login attempts of the user
    private int getLoginAttempts(){
        String query = "select login_attempts from users where username=?" ;
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("login_attempts");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // update the login attempts of the user
    private void updateLoginAttempts(int value){
        String query = "update users set login_attempts=? where username=?" ;
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, user.getUsername());
    
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // lock user account    
    private void lockUserAccount(){

        String query = "update users set isBlocked=1 where username=?" ;
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, user.getUsername());
    
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // authenticating the user
    private boolean checkUser(){
        String query = "select user_id,randomSalt,password,userType from users where username=? and isBlocked=0" ;
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
                else if (userType.equals("borrower")) {
                    isBorrower = true;
                }
                else if (userType.equals("lender")) {
                    isLender = true;
                }
        
                user.setUserType(userType);
                user.setUserId(resultSet.getInt("user_id"));
                
                return HashPassword.verifyPassword(user.getPassword(), pwd, salt);
            }    
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String execute(){

        int login_attempts = getLoginAttempts();

        if (login_attempts > MAX_LOGIN_ATTEMPTS) {
            lockUserAccount();
            System.out.println("Account locked due to failed login attempts");
            return "error";
        }
        
        if (checkUser()) {
            HttpSession session = ServletActionContext.getRequest().getSession();
            session.setAttribute("userSession", user);
            updateLoginAttempts(0);
            if (isAdminUser) {
                return "admin_user";
            }
            else if (isVerifier) {
                return "verifier";
            }
            else if (isBorrower) {
                return "borrower";
            }
            else if (isLender) {
                return "lender";
            }
        }
        else{
            System.out.println("Login attempts count = " + login_attempts);
            updateLoginAttempts(login_attempts+1);
        }
        return "error";
    }
    

}
