package com.loanapp.actions.borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;

public class BorrowerProfileAction extends ActionSupport{

    // assets with their maximum value
    Map<String,Long> assetMap = new HashMap<>();

    private User user;
    private DatabaseConnection db = new DatabaseConnection();

    private String assets;
    private long annualIncome;
    private int userId;

    //setter 
    public void setUser(User user){
        this.user = user;
    }
    public void setAssets(String assets){
        this.assets = assets;
    }
    public void setAnnualIncome(long annualIncome){
        this.annualIncome = annualIncome;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }

    //getter
    public User getUser(){
        return user;
    }
    public String getAssets(){
        return assets;
    }
    public long getAnnualIncome(){
        return annualIncome; 
    }
    public int getUserId(){
        return userId;
    }

    // inserting the values of the assets 
    private void generateAssetMap(){
        assetMap.put("car", (long)400000);
        assetMap.put("house", (long)5000000);
        assetMap.put("gold", (long)1000000);
        assetMap.put("savings", (long)200000);
    }

    // method for getting the user information 
    private void getUserInfo(String username){
        String query = "select emailId,mobileNo,gender,userType,annual_income,assets from users inner join borrowers " +
                        "on users.user_id = borrowers.user_id where username=?";
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
                user.setAnnualIncome(resultSet.getLong(5));
                user.setAssets(resultSet.getString(6));
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // method used for updating the borrowers table with their income and asset details
    public String updateBorrowerTable(){
        String query = "update borrowers set annual_income = ?, assets = ?, max_loan_amount = ? where user_id = ?" ;
        User user = (User) ServletActionContext.getRequest().getSession(false).getAttribute("userSession");
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setLong(1, getAnnualIncome());
            preparedStatement.setString(2, getAssets());
            preparedStatement.setLong(3 , getMaxLoanAmount());
            preparedStatement.setInt(4, user.getUserId()) ;

            int result = preparedStatement.executeUpdate();

            return result > 0 ? "success" :  "error";
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    // calculating the maximum loan amount for the current user based on the annual income
    private long getMaxLoanAmount(){
        long maxLoanAmount = 0;
        long currentAssetSum = 0;
        String asset[] = getAssets().split(",");
        generateAssetMap();
        for(String value : asset){
            currentAssetSum += assetMap.get(value);
        }
        maxLoanAmount = Math.round(getAnnualIncome() * 0.60 + currentAssetSum * 0.4);
        return maxLoanAmount;
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
