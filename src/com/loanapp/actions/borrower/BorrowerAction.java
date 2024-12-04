package com.loanapp.actions.borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.Loan;
import com.loanapp.models.TransactionHistory;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

class EmiDetail{

    private long loanAmount;
    private long emiAmount;
    private long emiPaid;
    private long emiPending;
    private String date;
    private int loanId;

    // setters
    public void setLoanAmount(long loanAmount){
        this.loanAmount = loanAmount;
    }
    public void setEmiAmount(long emiAmount){
        this.emiAmount = emiAmount;
    }
    public void setEmiPaid(long emiPaid){
        this.emiPaid = emiPaid;
    }
    public void setEmiPending(long emiPending){
        this.emiPending = emiPending;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }

    // getters
    public long getLoanAmount(){
        return loanAmount;
    }
    public long getEmiAmount(){
        return emiAmount;
    }
    public long getEmiPaid(){
        return emiPaid;
    }
    public long getEmiPending(){
        return emiPending;
    }
    public String getDate(){
        return date;
    }
    public int getLoanId(){
        return loanId;
    }
}

public class BorrowerAction extends ActionSupport implements ModelDriven<Loan> {

    private DatabaseConnection db = new DatabaseConnection();
    private Loan loan;
    private ArrayList<Loan> loans = new ArrayList<>(); 

    private TransactionHistory transaction;
    private ArrayList<TransactionHistory> history = new ArrayList<>();

    private EmiDetail emiDetail;
    private ArrayList<EmiDetail> emiDetails = new ArrayList<>();

    private String username;
    private long currentBalance;
    private int userId;
    private long maxLoanAmount;
    private int borrowerId;
    

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
    public void setTransaction(TransactionHistory transaction){
        this.transaction = transaction;
    }
    public void setHistory(ArrayList<TransactionHistory> history){
        this.history = history;
    }
    public void setBorrowerId(int borrowerId){
        this.borrowerId = borrowerId;
    }
    public void setEmiDetails(ArrayList<EmiDetail> emiDetails){
        this.emiDetails = emiDetails;
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
    public TransactionHistory getTransaction(){
        return transaction;
    }
    public ArrayList<TransactionHistory> getHistory(){
        return history;
    }
    public int getBorrowerId(){
        return borrowerId;
    }
    public ArrayList<EmiDetail> getEmiDetails(){
        return emiDetails;
    }


    @Override
    public Loan getModel() {
        return loan;
    }
    

    // get the current balance amount from the user
    private void getAmountInfo(){
        String query = "select current_loan_balance, max_loan_amount,borrower_id from borrowers where user_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setCurrentBalance(resultSet.getLong(1));
                setMaxLoanAmount(resultSet.getLong(2));
                setBorrowerId(resultSet.getInt(3));
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // getting the loan info for the particular borrower
    public void getLoanStatusInfo(int userId){
        String query = "select requested_amount,loan_tenure_months,status,loan_type,loan_id from loans inner join borrowers" 
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
                loan.setLoanId(resultSet.getInt(5));
                
                // adding each loan onject to the array list
                loans.add(loan);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for displaying the transaction history to the particular user
    public void showTransactionHistory(int userId){
        String query = "select amount, transaction_type, DATE_FORMAT(transaction_date, '%d/%m/%y'), lender_id from transaction_history inner join users on transaction_history.user_id=users.user_id "
                        + " where users.user_id = ? and transaction_type not in ('Deposit')";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transaction = new TransactionHistory();
                transaction.setAmount(resultSet.getLong(1));
                transaction.setTransactionType(resultSet.getString(2));
                transaction.setDate(resultSet.getString(3));

                String lender = getLenderName(resultSet.getInt(4));
                transaction.setName(lender);
    
                // adding to the history array list
                history.add(transaction);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Details cannot be fetched");
        }
    }

    // method for getting the lender name
    private String getLenderName(int lenderId){
        String query = "select username from users inner join lenders on users.user_id=lenders.user_id where lender_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, lenderId);
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


    // getting the information of the EMI'S of the borrower
    public void getEmiInfo(){
        String query = "select sum(emi_amount), sum(amount_paid), DATE_FORMAT(min(due_date), '%d/%m/%y' ), requested_amount, EMIs.loan_id from EMIs inner join "
                        +" loans on EMIs.loan_id = loans.loan_id where borrower_id = ? and EMIs.status='Due' group by EMIs.loan_id";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getBorrowerId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                emiDetail = new EmiDetail();

                long emi = resultSet.getLong(1);
                long paid = resultSet.getLong(2);
                long pending = emi - paid;

                emiDetail.setEmiAmount(emi);
                emiDetail.setEmiPaid(paid);
                emiDetail.setEmiPending(pending);
                emiDetail.setDate(resultSet.getString(3));
                emiDetail.setLoanAmount(resultSet.getLong(4));
                emiDetail.setLoanId(resultSet.getInt(5));

                emiDetails.add(emiDetail);
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
                setUserId(sessionUser.getUserId());

                getAmountInfo();
                getEmiInfo();
                getLoanStatusInfo(getUserId());
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