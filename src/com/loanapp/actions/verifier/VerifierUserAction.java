package com.loanapp.actions.verifier;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.loanapp.DatabaseConnection;

public class VerifierUserAction{
    
    private DatabaseConnection db = new DatabaseConnection();
    private String username;
    private String query;

    // setters
    public void setUsername(String username){
        this.username = username;
    }
    public void setQuery(String query){
        this.query = query;
    }
    

    // getters
    public String getUsername(){
        return username;
    }
    public String getQuery(){
        return query;
    }
    
    
    // method for allowing a user from blocked state
    public String allowUser(){
        String q = "update users set isBlocked=0 where username=?";
        setQuery(q);
        return userOperation();
    }

    //method for blocking the user
    public String blockUser(){
        String q = "update users set isBlocked=1 where username=?";
        setQuery(q);
        return userOperation();
    }

    public String userOperation() {
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, getUsername());
            int value = preparedStatement.executeUpdate();

            if (value > 0) {
                return "success";
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }


}
