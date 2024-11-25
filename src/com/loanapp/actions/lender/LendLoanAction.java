package com.loanapp.actions.lender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;

public class LendLoanAction extends ActionSupport{

    private DatabaseConnection db = new DatabaseConnection();

    private String username;
    private String borrowerName;
    private int interestRate;
    private long grantLoanAmount;
    private long requestedAmount;
    private long balanceAmount;
    private int loanMonth;
    private int loanId;

    // setters
    public void setUsername(String username){
        this.username = username;
    }
    public void setBorrowerName(String borrowerName){
        this.borrowerName = borrowerName;
    }
    public void setInterestRate(int interestRate){
        this.interestRate = interestRate;
    }
    public void setGrantLoanAmount(long grantLoanAmount){
        this.grantLoanAmount = grantLoanAmount;
    }
    public void setRequestedAmount(long requestedAmount){
        this.requestedAmount = requestedAmount;
    }
    public void setBalanceAmount(long balanceAmount){
        this.balanceAmount = balanceAmount;
    }
    public void setLoanMonth(int loanMonth){
        this.loanMonth = loanMonth;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }

    // getter
    public String getUsername(){
        return username;
    }
    public String getBorrowerName(){
        return borrowerName;
    }
    public int getInterestRate(){
        return interestRate;
    }
    public long getGrantLoanAmount(){
        return grantLoanAmount;
    }
    public long getRequestedAmount(){
        return requestedAmount;
    }
    public long getBalanceAmount(){
        return balanceAmount;
    }
    public int getLoanMonth(){
        return loanMonth;
    }
    public int getLoanId(){
        return loanId;
    }

    private boolean checkForGrantLoan(){
    
        long grantAmount = getGrantLoanAmount();
        long reqAmount = getRequestedAmount();

        // checking for the correct requested amount or 10% of the requested amount 
        if ((grantAmount == reqAmount) || (grantAmount > reqAmount*0.1 && grantAmount <= reqAmount)) { 
            balanceAmount = reqAmount - grantAmount;
            setBalanceAmount(balanceAmount);
            return true;
        }

        return false;
    }

    private int getIdFromUser(String name , String userType){
        
        String query = "select " ;
        if (userType.equals("lender")) {
            query += "lender_id from lenders ";
        }
        else{
            query += "borrower_id from borrowers ";
        }
        query += "where user_id = (select user_id from users where username = ?)";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void loanAccept(){
        String query = "insert into loan_distribution (loan_id, lender_id, borrower_id, interest_rate, loan_grant_amount, balance_amount)"
                        + " values (?,?,?,?,?,?) ";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setInt(1, getLoanId());
            preparedStatement.setInt(2, getIdFromUser(getUsername(),"lender"));
            preparedStatement.setInt(3, getIdFromUser(getBorrowerName(),"borrower"));
            preparedStatement.setInt(4, getInterestRate());
            preparedStatement.setLong(5, getGrantLoanAmount());
            preparedStatement.setLong(6, getBalanceAmount());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                
                System.out.println("Loan accepted");
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
                setUsername(sessionUser.getUsername());

                if (checkForGrantLoan()) {
                    loanAccept();
                }

                return "success";
            }
        }
        return "error";
    }
}
