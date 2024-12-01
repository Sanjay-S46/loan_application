package com.loanapp.actions.borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private long remainingPrinciple;
    private int remainingMonths;
    private int interestRate;
    private int emi;

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
    public void setRemainingPrinciple(long remainingPrinciple){
        this.remainingPrinciple = remainingPrinciple;
    }
    public void setRemainingMonths(int remainingMonths){
        this.remainingMonths = remainingMonths;
    }
    public void setInterestRate(int interestRate){
        this.interestRate = interestRate;
    }
    public void setEmi(int emi){
        this.emi = emi;
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
    public long getRemainingPrinciple(){
        return remainingPrinciple;
    }
    public int getRemainingMonths(){
        return remainingMonths;
    }
    public int getInterestRate(){
        return interestRate;
    }
    public int getEmi(){
        return emi;
    }
}

public class PaymentAction extends ActionSupport {

    private DatabaseConnection db = new DatabaseConnection();
    private History history ;

    private int userId;
    private long payingAmount; 
    private long emiAmount;
    private long emiPending;
    private int loanId;
    private int lenderId;
    private int lenderCount;
    
    private ArrayList<Integer> lenderList = new ArrayList<>();
    private ArrayList<History> historyList = new ArrayList<>();
    private Map<Integer,Long> emiMap = new HashMap<>();

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
    public void setEmiPending(long emiPending){
        this.emiPending = emiPending;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }
    public void setLenderId(int lenderId){
        this.lenderId = lenderId;
    }
    public void setLenderList(ArrayList<Integer> lenderList){
        this.lenderList = lenderList;
    }
    public void setHistoryList(ArrayList<History> historyList){
        this.historyList = historyList;
    }
    public void setLenderCount(int lenderCount){
        this.lenderCount = lenderCount;
    }
    public void setEmiMap(Map<Integer,Long> emiMap){
        this.emiMap = emiMap;
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
    public long getEmiPending(){
        return emiPending;
    }
    public int getLoanId(){
        return loanId;
    }
    public int getLenderId(){
        return lenderId;
    }
    public ArrayList<Integer> getLenderList(){
        return lenderList;
    }
    public ArrayList<History> getHistoryList(){
        return historyList;
    }
    public int getLenderCount(){
        return lenderCount;
    }
    public Map<Integer,Long> getEmiMap(){
        return emiMap;
    }

    // method for getting all the details 
    public void getAllInfo(int loanId){
        String query = "select loan_distribution.lender_id, loan_distribution.borrower_id, lenders.user_id, borrowers.user_id, "
                    +" remaining_principle_amount, remaining_months, interest_rate, lender_list, emi from "
                    +" loan_distribution inner join loans on loan_distribution.loan_id=loans.loan_id inner join lenders on  "
                    +" loan_distribution.lender_id=lenders.lender_id inner join borrowers on loan_distribution.borrower_id = "
                    +" borrowers.borrower_id where is_loan_accepted=1 and loan_distribution.loan_id=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, loanId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                
                String lenders[] = resultSet.getString(8).split(",");
                setLenderCount(lenders.length);
    
                for (int i = 0; i < getLenderCount(); i++) {
                    lenderList.add(Integer.parseInt(lenders[i]));
                }
    
                do {
                    history = new History();

                    history.setLenderId(resultSet.getInt(1));
                    history.setBorrowerId(resultSet.getInt(2));
                    history.setLenderUserId(resultSet.getInt(3));
                    history.setBorrowerUserId(resultSet.getInt(4));
                    history.setRemainingPrinciple(resultSet.getLong(5));
                    history.setRemainingMonths(resultSet.getInt(6));
                    history.setInterestRate(resultSet.getInt(7));
                    history.setEmi(resultSet.getInt(9));
    
                    historyList.add(history);
                } 
                while (resultSet.next());  
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
        long pending = getEmiPending();
        boolean isSingleLender = checkForSingleLender();

        // correct amount payment
        if (amount == pending) {
            updateEmiStatus("Paid");
            updateLoanDistribution();
        }
        // lesser amount payment
        else if (amount < pending) {
            if (!isSingleLender) {
                splitAmount(amount);
            }
            updateEmiStatus("Due");
        }
        // over amount payment 
        else {
            if (!isSingleLender) {
                splitAmount(amount);
            }
            updateEmiStatus("Paid");
            deductFromPrincipleAmount();
            updateLoanDistribution();
        }

        updateLenderEarning();
        updateTransactionHistory("borrower");
        updateTransactionHistory("lender");

        return "success" ;
    }


    // method for splitting the amount if more than one lenders
    private void splitAmount(long amount){

        Map<Integer,Long> emiMap = getEmiMap();
        double emiAmount = (double) getEmiAmount();
        double payingAmount = (double) amount;

        for(Integer emiId : emiMap.keySet()){
            double share = ((double)emiMap.get(emiId)/emiAmount) * payingAmount;
            share = Math.round(share);
            emiMap.put(emiId, (long)share);
        }

        setEmiMap(emiMap);
    }


    // method for getting the emi id of particular loans
    public void getEmiIdsOfLoan(){
        String query = "select emi_id,emi_amount from EMIs where loan_id = ? and status='Due'";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getLoanId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int emiID = resultSet.getInt(1);
                long amount = resultSet.getLong(2);
                emiMap.put(emiID, amount);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method used for checking if it is a single lender 
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

            Iterator<Map.Entry<Integer,Long>> mapIterator = emiMap.entrySet().iterator();
            Iterator<History> listIterator = historyList.iterator();

            while (mapIterator.hasNext() && listIterator.hasNext()) {

                Map.Entry<Integer,Long> entry = mapIterator.next();
                History history = listIterator.next();

                long amount = entry.getValue();
                int lenderId = history.getLenderId();

                preparedStatement.setLong(1, amount);
                preparedStatement.setInt(2, lenderId);

                int result = preparedStatement.executeUpdate();

                if (result > 0) {
                    System.out.println("Total earnings of the lender updated with id = " + lenderId);
                }
            }

        } 
        catch (Exception e) {
            e.printStackTrace();
        }   
    }

    private void updateEmiStatus(String status){
        String query = "update EMIs set amount_paid=?, status=? where emi_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            for (Integer emiId : emiMap.keySet()) {

                preparedStatement.setLong(1, emiMap.get(emiId));
                preparedStatement.setString(2, status);
                preparedStatement.setInt(3, (int)emiId);

                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    System.out.println("EMI status has been updated for emi id = " + emiId);
                }
            }
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
        
            Iterator<Map.Entry<Integer,Long>> mapIterator = emiMap.entrySet().iterator();
            Iterator<History> listIterator = historyList.iterator();

            while (mapIterator.hasNext() && listIterator.hasNext()) {

                Map.Entry<Integer,Long> entry = mapIterator.next();
                History history = listIterator.next();

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
                preparedStatement.setLong(4, entry.getValue()); 

                int result = preparedStatement.executeUpdate();

                if (result > 0) {
                    System.out.println("Transaction history updated successfully for " + role);
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method used for updating the loan distribution table
    private void updateLoanDistribution(){
        String query = "update loan_distribution set remaining_principle_amount=?, remaining_months=? where lender_id=? and loan_id=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setInt(4, getLoanId());

            for(History history : historyList){

                long principleAmount = history.getRemainingPrinciple();
                int months = history.getRemainingMonths();
                long amount = (long)Math.round(principleAmount - principleAmount/months);

                preparedStatement.setLong(1, amount);
                preparedStatement.setInt(2, months - 1);
                preparedStatement.setInt(3, history.getLenderId());

                int result = preparedStatement.executeUpdate();

                if (result > 0) {
                    System.out.println("Remaining principle amount and months are updated for lender id = " + history.getLenderId() 
                                    + " of loan id = " + getLoanId());
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for updating the principle amount
    private void deductFromPrincipleAmount(){
        String query = "update loan_distribution set remaining_principle_amount = ? where lender_id = ? "
                        + " and loan_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(3, getLoanId());

            Iterator<Map.Entry<Integer,Long>> mapIterator = emiMap.entrySet().iterator();
            Iterator<History> listIterator = historyList.iterator();

            while (mapIterator.hasNext() && listIterator.hasNext()) {

                Map.Entry<Integer,Long> entry = mapIterator.next();
                History history = listIterator.next();
                
                long excessAmount = entry.getValue() - history.getEmi();
                long newPrinciple = history.getRemainingPrinciple() - excessAmount ;
                System.out.println("New principle amount = " + newPrinciple);

                int lenderId = history.getLenderId();
                preparedStatement.setLong(1, newPrinciple);
                preparedStatement.setInt(2, lenderId);

                int result = preparedStatement.executeUpdate();

                if (result > 0) {
                    history.setRemainingPrinciple(newPrinciple);
                    int interestRate = history.getInterestRate();
                    int months = history.getRemainingMonths();

                    System.out.println("Principle amount has been deducted from lender id = " + lenderId);
                    recalculateEMI(newPrinciple, interestRate, months, lenderId);
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for recalculating the EMI if paid extra amount
    private void recalculateEMI(long newPrinciple, int interestRate, int months, int lenderId){

        double principal = (double) newPrinciple;
        double interest = (double) interestRate;
        double monthlyInterestRate = interest/12/100;

        double value = Math.pow(1+monthlyInterestRate,months);
        double emiValue = principal * monthlyInterestRate * value / (value-1);

        int emi = (int)Math.ceil(emiValue); 

        String query = "update loan_distribution set emi = ? where lender_id = ? and loan_id = ?";

        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setInt(1, emi);
            preparedStatement.setInt(2, lenderId);
            preparedStatement.setInt(3, getLoanId());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("EMI recalculated and updated for lender id = " + lenderId);
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
