package com.loanapp.actions.borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;

class History {

    private int lenderId;
    private int borrowerId;
    private int lenderUserId;
    private int borrowerUserId;

    // setters
    public void setLenderId(int lenderId){
        this.lenderId = lenderId;
    }
    public void setBorrowerId(int borrowerId){
        this.borrowerId = borrowerId;
    }
    public void setLenderUserId(int lenderUserId){
        this.lenderUserId = lenderUserId;
    }
    public void setBorrowerUserId(int borrowerUserId){
        this.borrowerUserId = borrowerUserId;
    }

    // getters
    public int getLenderId(){
        return lenderId;
    }
    public int getBorrowerId(){
        return borrowerId;
    }
    public int getLenderUserId(){
        return lenderUserId;
    }
    public int getBorrowerUserId(){
        return borrowerUserId;
    }
}

public class PaymentAction extends ActionSupport {

    private DatabaseConnection db = new DatabaseConnection();
    private History history ;

    private int userId;
    private long payingAmount; 
    private long emiAmount;
    private int loanId;
    private int lenderId;
    private ArrayList<Integer> emiIdList = new ArrayList<>();
    private ArrayList<History> historyList = new ArrayList<>();

    // setter 
    public void setUserId(int userId){
        this.userId = userId;
    }
    public void setPayingAmount(long payingAmount){
        this.payingAmount = payingAmount;
    }
    public void setEmiAmount(long emiAmount){
        this.emiAmount = emiAmount;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }
    public void setLenderId(int lenderId){
        this.lenderId = lenderId;
    }
    public void setEmiIdList(ArrayList<Integer> emiIdList){
        this.emiIdList = emiIdList;
    }
    public void setHistoryList(ArrayList<History> historyList){
        this.historyList = historyList;
    }

    // getters
    public int getUserId(){
        return userId;
    }
    public long getPayingAmount(){
        return payingAmount;
    }
    public long getEmiAmount(){
        return emiAmount;
    }
    public int getLoanId(){
        return loanId;
    }
    public int getLenderId(){
        return lenderId;
    }
    public ArrayList<Integer> getEmiIdList(){
        return emiIdList;
    }
    public ArrayList<History> getHistoryList(){
        return historyList;
    }

    // method for getting all the details 
    public void getAllInfo(int loanId){
        String query = "select loan_distribution.lender_id, loan_distribution.borrower_id, lenders.user_id, borrowers.user_id from "
                    +" loan_distribution inner join loans on loan_distribution.loan_id=loans.loan_id inner join lenders on  "
                    +" loan_distribution.lender_id=lenders.lender_id inner join borrowers on loan_distribution.borrower_id = "
                    +" borrowers.borrower_id where is_loan_accepted=1 and loan_distribution.loan_id=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, loanId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                
                history = new History();

                history.setLenderId(resultSet.getInt(1));
                history.setBorrowerId(resultSet.getInt(2));
                history.setLenderUserId(resultSet.getInt(3));
                history.setBorrowerUserId(resultSet.getInt(4));

                historyList.add(history);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this is the first method called on executing the payment action
    public String amountPaymentTask(){

        getAllInfo(getLoanId());
        getEmiIdsOfLoan();

        long amount = getPayingAmount();
        long emiAmount = getEmiAmount();
        boolean isSingleLender = checkForSingleLender();

        // correct amount payment
        if (amount == emiAmount) {
            if (isSingleLender == true) {
                updateEmiStatus("Paid");
            }
            else{
                // splitAmount();
            }
        }
        // lesser amount payment
        else if (amount < emiAmount) {
            if (!isSingleLender) {
                
            }
        }
        // over amount payment 
        else {
            if (isSingleLender == true) {
                updateEmiStatus("Paid");
            }
            else{
                
            } 
        }

        updateLenderEarning();
        updateTransactionHistory("borrower");
        updateTransactionHistory("lender");

        return "success" ;
    }

    // method for getting the emi id of particular loans
    public void getEmiIdsOfLoan(){
        String query = "select emi_id from EMIs where loan_id = ? and status='Due'";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getLoanId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int emiID = resultSet.getInt(1);
                emiIdList.add(emiID);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean checkForSingleLender(){
        String query = "select lender_id from loan_distribution where loan_id=(select loan_id from EMIs where loan_id=? and "
                    + " status='Due' group by loan_id having count(loan_id)=1) ";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setInt(1, getLoanId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setLenderId(resultSet.getInt(1));
                return true;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateLenderEarning(){
        String query = "update lenders set total_earnings = total_earnings + ? where lender_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setLong(1, getEmiAmount());
            preparedStatement.setInt(2, getLenderId());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Total earnings of the lender updated");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }   
        
    }

    private void updateEmiStatus(String status){
        String query = "update EMIs set status=? where emi_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            for (Integer emiId : emiIdList) {
                preparedStatement.setString(1, status);
                preparedStatement.setInt(2, emiId);

                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    System.out.println("EMI status has been updated for emi id = " + emiId);
                }
            }

            setEmiIdList(null);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }   
    }

    private void updateTransactionHistory(String role){
        String query = "insert into transaction_history (user_id, lender_id, borrower_id, amount, transaction_type) values (?,?,?,?,?)";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
        
            for(History history : historyList){
                if (role.equals("borrower")) {
                    preparedStatement.setInt(1, history.getBorrowerUserId());
                    preparedStatement.setString(5,"EMI Paid");
                }
                else{
                    preparedStatement.setInt(1, history.getLenderUserId());
                    preparedStatement.setString(5,"EMI Received");
                }
                preparedStatement.setInt(2, history.getLenderId());
                preparedStatement.setInt(3, history.getBorrowerId());
                preparedStatement.setLong(4, getEmiAmount()); // needs to be updated..

                int result = preparedStatement.executeUpdate();

                if (result > 0) {
                    System.out.println("Transaction history updated successfully");
                }
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

                setUserId(sessionUser.getUserId());
            
                return "success";
            }
        }
        return "error";
    }
}
