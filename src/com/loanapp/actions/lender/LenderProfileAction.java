package com.loanapp.actions.lender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;

public class LenderProfileAction extends ActionSupport{

    private User user;
    private DatabaseConnection db = new DatabaseConnection();

    private long depositAmount;

    //setter 
    public void setUser(User user){
        this.user = user;
    }
    public void setDepositAmount(long depositAmount){
        this.depositAmount = depositAmount;
    }

    //getter
    public User getUser(){
        return user;
    }
    public long getDepositAmount(){
        return depositAmount;
    }

    // method for getting the user information 
    private void getUserInfo(String username){
        String query = "select emailId,mobileNo,gender,userType,available_funds from users inner join lenders " +
                        "on users.user_id = lenders.user_id where username=?";
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
                user.setAvailableFunds(resultSet.getLong(5));
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method for getting the available fund amount 
    private long getAvailableFundAmount(int userId){
        String query = "select available_funds from lenders where user_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // method for depositing the amount give by the lender
    public String depositAmount(){
        String query = "update lenders set available_funds = ? where user_id = ?";
        User user = (User) ServletActionContext.getRequest().getSession(false).getAttribute("userSession"); 
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            long deposit = getDepositAmount();
            long totalAmount = deposit + getAvailableFundAmount(user.getUserId());
            preparedStatement.setLong(1, totalAmount);
            preparedStatement.setInt(2, user.getUserId());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                insertIntoTransactionTable(user.getUserId(),deposit);
                return "success";
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    // method for inserting the deposit amount in the transaction table
    private void insertIntoTransactionTable(int userId,long deposit){
        String query = "insert into transaction_history (user_id , lender_id, amount, transaction_type) " + 
                        "values (?,(select lender_id from lenders where user_id = ?),?,?)";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setLong(3, deposit);
            preparedStatement.setString(4, "Deposit");

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Successfully inserted to transaction table");
            }
            else{
                System.out.println("Failed to insert in transaction table");
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
        }
        return "error";
    }
}
