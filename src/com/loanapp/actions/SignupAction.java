package com.loanapp.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class SignupAction extends ActionSupport implements ModelDriven<User>{
    
    private DatabaseConnection db = new DatabaseConnection();
    private User user = new User();

    @Override
    public User getModel() {
        return user;
    }

    private boolean isExistingUser(){
        String query = "select username from users where username = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                System.out.println("Valid user verified..");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean insert(){
        String query = "insert into users (username, emailId, mobileNo, gender, userType) values(?,?,?,?,?)";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ) {
            //setting the values
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmailId());
            preparedStatement.setString(3, user.getMobileNo());
            preparedStatement.setString(4, user.getGender());
            preparedStatement.setString(5, user.getUserType());

            int result = preparedStatement.executeUpdate();
            
            if (result > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);  // retrieve the auto-generated user_id
                        addToVerificationTable(userId);
                    }

                }
                return true;
            }
            
        } 
        catch (Exception e) {
            e.printStackTrace();    
        }
        return false;
    }

    private void addToVerificationTable(int userId){
        String query = "insert into verification (user_id) values(?)";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            System.out.println("Inserted into verification table...");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not insert into verification table...");
        }
    }

    @Override
    public String execute(){

        if (!isExistingUser()) {
            return "error";
        }
        if (!insert()) {
            return "error";
        }
        
        return "success";
    }
}