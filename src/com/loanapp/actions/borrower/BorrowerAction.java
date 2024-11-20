package com.loanapp.actions.borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.Loan;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class BorrowerAction extends ActionSupport implements ModelDriven<Loan>{

    private DatabaseConnection db = new DatabaseConnection();
    private Loan loan;

    private String username;
    private long currentBalance;
    private int userId;
    private long maxLoanAmount;
    private ArrayList<Loan> loans = new ArrayList<>();


    // setters
    public void setUsername(String username){
        this.username = username;
    }
    public void setCurrentBalance(long currentBalance){
        this.currentBalance = currentBalance;
    }
    public void setMaxLoanAmount(long maxLoanAmount){
        this.maxLoanAmount = maxLoanAmount;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }
    public void setLoan(Loan loan){
        this.loan = loan;
    }
    public void setLoans(ArrayList<Loan> loans){
        this.loans = loans;
    }

    // getters
    public String getUsername(){
        return username;
    }
    public long getCurrentBalance(){
        return currentBalance;
    }
    public long getMaxLoanAmount(){
        return maxLoanAmount;
    }
    public int getUserId(){
        return userId;
    }
    public Loan getLoan(){
        return loan;
    }
    public ArrayList<Loan> getLoans(){
        return loans;
    }

    @Override
    public Loan getModel() {
        return loan;
    }
    

    // get the current balance amount from the user
    private void getAmountInfo(){
        String query = "select current_loan_balance,max_loan_amount from borrowers where user_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setCurrentBalance(resultSet.getLong(1));
                setMaxLoanAmount(resultSet.getLong(2));
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // getting the loan info for the particular borrower
    public void getLoanInfo(int userId){
        String query = "select requested_amount,loan_tenure_months,status,loan_type from loans inner join borrowers" 
                        + " on loans.borrower_id = borrowers.borrower_id where user_id = ?";

        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                loan = new Loan();
                loan.setLoanAmount(resultSet.getLong(1));
                loan.setLoanMonth(resultSet.getInt(2));
                loan.setStatus(resultSet.getString(3));
                loan.setLoanType(resultSet.getString(4));
                loan.setUserId(userId);
                
                // adding each loan onject to the array list
                loans.add(loan);
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

                setUsername(sessionUser.getUsername().toUpperCase());
                setUserId(sessionUser.getUserId());

                getAmountInfo();
                getLoanInfo(getUserId());
                return "success";
            }
            else{
                System.out.println("Username not available");
            }
        }
        return "error";
    }
}