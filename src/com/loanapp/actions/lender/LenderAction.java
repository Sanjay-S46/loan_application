package com.loanapp.actions.lender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.TransactionHistory;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;

// MODEL CLASSES
// =================================================================================================================================

// class used for storing the loan details about the borrowers

class LoanDetail{
    private String username;
    private long requestedAmount;
    private String loanPurpose;
    private int loanMonth;
    private String status;
    private int loanId;
    private long balanceAmount;

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
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }
    public void setBalanceAmount(long balanceAmount){
        this.balanceAmount = balanceAmount;
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
    public int getLoanId(){
        return loanId;
    }
    public long getBalanceAmount(){
        return balanceAmount;
    }
}

// ===============================================================================================================================

public class LenderAction extends ActionSupport{

    private DatabaseConnection db = new DatabaseConnection();
    private LoanDetail loanDetail;
    private TransactionHistory transaction;
    private ArrayList<LoanDetail> loanDetails = new ArrayList<>();
    private ArrayList<TransactionHistory> history = new ArrayList<>();

    private String username;
    private long availableFunds;
    private int userId;
    private int lenderId;

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
    public void setTransaction(TransactionHistory transaction){
        this.transaction = transaction;
    }
    public void setHistory(ArrayList<TransactionHistory> history){
        this.history = history;
    }
    public void setLenderId(int lenderId){
        this.lenderId = lenderId;
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
    public TransactionHistory getTransaction(){
        return transaction;
    }
    public ArrayList<TransactionHistory> getHistory(){
        return history;
    }
    public int getLenderId(){
        return lenderId;
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

    // getting the lender id of current user
    private void getLenderIdOfUser(int userId){
        String query = "select lender_id from lenders where user_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setLenderId(resultSet.getInt(1));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // method used for getting the loan requests
    private void getLoanRequests(){
        String query = "select username, requested_amount, loan_purpose, loan_tenure_months, status, loans.loan_id, lender_list, "
                    +" balance_amount from loans inner join borrowers on loans.borrower_id=borrowers.borrower_id inner join users on "
                    +" borrowers.user_id=users.user_id where status not in ('Fully Funded','Closed')";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                String lenderListStr = resultSet.getString(7); 
                boolean skipLoan = false;

                if (lenderListStr != null) {
                    String[] lenderList = lenderListStr.split(",");
                    for (String lender : lenderList) {
                        if (Integer.parseInt(lender.trim()) == getLenderId()) {
                            skipLoan = true; 
                            break; 
                        }
                    }
                }

                if (skipLoan) {
                    continue;
                }

                loanDetail = new LoanDetail();
                loanDetail.setUsername(resultSet.getString(1));
                loanDetail.setRequestedAmount(resultSet.getLong(2));
                loanDetail.setLoanPurpose(resultSet.getString(3));
                loanDetail.setLoanMonth(resultSet.getInt(4));
                loanDetail.setStatus(resultSet.getString(5));
                loanDetail.setLoanId(resultSet.getInt(6));
                loanDetail.setBalanceAmount(resultSet.getLong(8));

                //adding to the array list
                loanDetails.add(loanDetail);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for calculating the balance amount

    
    // method for displaying the transaction history for the lender
    public void showTransactionHistory(int userId){
        String query = "select * from transaction_history inner join users on transaction_history.user_id=users.user_id "
                        + " where users.user_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                transaction = new TransactionHistory();
                transaction.setAmount(resultSet.getLong("amount"));
                transaction.setTransactionType(resultSet.getString("transaction_type"));
                transaction.setDate(resultSet.getDate("transaction_date"));

                if (transaction.getTransactionType().equals("Deposit")) {
                    transaction.setName(getUsername() + " (You)");
                }
                else{
                    String borrowerName = getBorrowerName(resultSet.getInt("borrower_id"));
                    transaction.setName(borrowerName);
                }

                // adding to the history array list
                history.add(transaction);
            }
            
            System.out.println("Transaction history displayed.....");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Details cannot be fetched");
        }
    }

    // method for getting the borrower name
    private String getBorrowerName(int borrowerId){
        String query = "select username from users inner join borrowers on users.user_id=borrowers.user_id where borrower_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, borrowerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // method used to display the lending status of the loans given by the lender
    private void showLendingStatus(int userId){
        String query = "select loans.borrower_id, updated_at, interest_rate, loan_grant_amount, DATE_ADD(updated_at, INTERVAL "
                    +" loan_tenure_months MONTH) AS due_date from loan_distribution inner join loans on loan_distribution.loan_id = "
                    +" loans.loan_id inner join lenders on lenders.lender_id=loan_distribution.lender_id where lenders.user_id = ? ";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            
            
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
                setUserId(sessionUser.getUserId());

                getAmountInfo();
                getLenderIdOfUser(getUserId());
                showLendingStatus(getUserId());
                getLoanRequests();
                showTransactionHistory(getUserId());


                return "success";
            }
            else{
                System.out.println("Username not available");
            }
        }
        return "error";
    }
}
