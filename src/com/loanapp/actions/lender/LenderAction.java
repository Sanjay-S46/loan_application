package com.loanapp.actions.lender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;

// class used for storing the loan details about the borrowers
class LoanDetail{
    private String username;
    private long requestedAmount;
    private String loanPurpose;
    private int loanMonth;
    private String status;

    // setters
    public void setUsername(String username){
        this.username = username;
    }
    public void setRequestedAmount(long requestedAmount){
        this.requestedAmount = requestedAmount;
    }
    public void setLoanPurpose(String loanPurpose){
        this.loanPurpose = loanPurpose;
    }
    public void setLoanMonth(int loanMonth){
        this.loanMonth = loanMonth;
    }
    public void setStatus(String status){
        this.status = status;
    }

    // getters
    public String getUsername(){
        return username;
    }
    public long getRequestedAmount(){
        return requestedAmount;
    }
    public String getLoanPurpose(){
        return loanPurpose;
    }
    public int getLoanMonth(){
        return loanMonth;
    }
    public String getStatus(){
        return status;
    }
}

public class LenderAction extends ActionSupport{

    private DatabaseConnection db = new DatabaseConnection();
    private LoanDetail loanDetail;
    private ArrayList<LoanDetail> loanDetails = new ArrayList<>();

    private String username;
    private long availableFunds;
    private int userId;

    // setters
    public void setUsername(String username){
        this.username = username;
    }
    public void setAvailableFunds(long availableFunds){
        this.availableFunds = availableFunds;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }
    public void setLoanDetail(LoanDetail loanDetail){
        this.loanDetail = loanDetail;
    }
    public void setLoanDetails(ArrayList<LoanDetail> loanDetails){
        this.loanDetails = loanDetails;
    }

    // getters
    public String getUsername(){
        return username;
    }
    public long getAvailableFunds(){
        return availableFunds;
    }
    public int getUserId(){
        return userId;
    }
    public LoanDetail getLoanDetail(){
        return loanDetail;
    }
    public ArrayList<LoanDetail> getLoanDetails(){
        return loanDetails;
    }

    // method used for setting the username and fund amount to the home page of the lender
    private void getAmountInfo(){
        String query = "select available_funds from lenders where user_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setInt(1, getUserId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                setAvailableFunds(resultSet.getLong(1));
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method used for getting the loan requests
    private void getLoanRequests(){
        String query = "select username, requested_amount, loan_purpose, loan_tenure_months, status from loans join borrowers on "
            + " borrowers.borrower_id = loans.borrower_id join users on borrowers.user_id = users.user_id";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                loanDetail = new LoanDetail();
                loanDetail.setUsername(resultSet.getString(1).toUpperCase());
                loanDetail.setRequestedAmount(resultSet.getLong(2));
                loanDetail.setLoanPurpose(resultSet.getString(3));
                loanDetail.setLoanMonth(resultSet.getInt(4));
                loanDetail.setStatus(resultSet.getString(5));

                //adding to the arry list
                loanDetails.add(loanDetail);
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

                setUsername(sessionUser.getUsername().toUpperCase());
                setUserId(sessionUser.getUserId());

                getAmountInfo();
                getLoanRequests();

                return "success";
            }
            else{
                System.out.println("Username not available");
            }
        }
        return "error";
    }
}
