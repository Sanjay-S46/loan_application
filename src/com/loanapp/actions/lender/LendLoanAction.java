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
    private int loanMonth;
    private int loanId;
    private int userId;

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
    public void setLoanMonth(int loanMonth){
        this.loanMonth = loanMonth;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }
    public void setUserId(int userId){
        this.userId = userId;
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
    public int getLoanMonth(){
        return loanMonth;
    }
    public int getLoanId(){
        return loanId;
    }
    public int getUserId(){
        return userId;
    }

    private boolean checkForGrantLoan(){
    
        long availableAmount = getAvailableAmount(getUserId());
        long grantAmount = getGrantLoanAmount();
        long reqAmount = getRequestedAmount();

        if (availableAmount < grantAmount) {
            return false;
        }

        // checking for the correct requested amount or 10% of the requested amount 
        if ((grantAmount == reqAmount) || (grantAmount > reqAmount*0.1 && grantAmount <= reqAmount)) { 
            return true;
        }

        return false;
    }

    private long getAvailableAmount(int userId){
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
        return -1;
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

    private int calculateEMI(long amount, int interestRate,int months){

        double principal = (double) amount;
        double interest = (double) interestRate;
        double monthlyInterestRate = interest/12/100;

        double value = Math.pow(1+monthlyInterestRate,months);
        double emi = principal * monthlyInterestRate * value / (value-1);
        return (int)Math.ceil(emi); 
    }

    private void loanAccept(){
        String query = "insert into loan_distribution (loan_id, lender_id, borrower_id, interest_rate, loan_grant_amount, "
                    + "remaining_principle_amount, emi, remaining_months) values (?,?,?,?,?,?,?,?) ";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            int lenderId = getIdFromUser(getUsername(),"lender");

            preparedStatement.setInt(1, getLoanId());
            preparedStatement.setInt(2, lenderId);
            preparedStatement.setInt(3, getIdFromUser(getBorrowerName(),"borrower"));
            preparedStatement.setInt(4, getInterestRate());
            preparedStatement.setLong(5, getGrantLoanAmount());
            preparedStatement.setLong(6, getGrantLoanAmount());
            int amount = calculateEMI(getGrantLoanAmount(), getInterestRate(), getLoanMonth());
            preparedStatement.setInt(7, amount);
            preparedStatement.setInt(8, getLoanMonth());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                insertLenderDetails(getLoanId(),lenderId);
                updateAvailableFunds(lenderId);
                System.out.println("Loan accepted");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // method for updating the available funds
    private void updateAvailableFunds(int lenderId){
        String query = "update lenders set available_funds = available_funds - ? where lender_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setLong(1, getGrantLoanAmount());
            preparedStatement.setInt(2, lenderId);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Lender's available amount updated..");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // inserting the lender id in the lender details
    private void insertLenderDetails(int loanId,int lenderId){
        String query = "update loans set lender_list = CONCAT(IFNULL(lender_list, ''), ?, ',')  where loan_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, lenderId);
            preparedStatement.setInt(2, loanId);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Inserted into lenders list");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override 
    public String execute(){    
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        if (session != null) {
            User sessionUser = (User) session.getAttribute("userSession");
            if (sessionUser!=null && sessionUser.getUsername() != null) {

                setUserId(sessionUser.getUserId());
                setUsername(sessionUser.getUsername());

                if (checkForGrantLoan()) {
                    loanAccept();
                    return "success";
                }
                else{
                    return "error";
                }
            }
        }
        return "error";
    }
}
