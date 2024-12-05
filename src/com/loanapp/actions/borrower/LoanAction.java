package com.loanapp.actions.borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;

public class LoanAction extends ActionSupport{

    private DatabaseConnection db = new DatabaseConnection();   

    private int loanId;
    private int emi;
    private long loanGrantedAmount;
    private int distributionId;
    private int lenderId;
    private int borrowerId;
    private long availableFunds;
    private long currentBalance;
    private int lenderUserId;
    private int borrowerUserId;
    private long maxLoanAmount;
    private int month;

    // setters
    public void setEmi(int emi){
        this.emi = emi;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }
    public void setLoanGrantedAmount(long loanGrantedAmount){
        this.loanGrantedAmount = loanGrantedAmount;
    }
    public void setDistributionId(int distributionId){
        this.distributionId = distributionId;
    }
    public void setLenderId(int lenderId){
        this.lenderId = lenderId;
    }
    public void setBorrowerId(int borrowerId){
        this.borrowerId = borrowerId;
    }
    public void setAvailableFunds(long availableFunds){
        this.availableFunds = availableFunds;
    }
    public void setCurrentBalance(long currentBalance){
        this.currentBalance = currentBalance;
    }
    public void setLenderUserId(int lenderUserId){
        this.lenderUserId = lenderUserId;
    }
    public void setBorrowerUserId(int borrowerUserId){
        this.borrowerUserId = borrowerUserId;
    }
    public void setMaxLoanAmount(long maxLoanAmount){
        this.maxLoanAmount = maxLoanAmount;
    }
    public void setMonth(int month){
        this.month = month;
    }

    // getters
    public int getEmi(){
        return emi;
    }
    public int getLoanId(){
        return loanId;
    }
    public long getLoanGrantedAmount(){
        return loanGrantedAmount;
    }
    public int getDistributionId(){
        return distributionId;
    }
    public int getLenderId(){
        return lenderId;
    }
    public int getBorrowerId(){
        return borrowerId;
    }
    public long getAvailableFunds(){
        return availableFunds;
    }
    public long getCurrentBalance(){
        return currentBalance;
    }
    public int getLenderUserId(){
        return lenderUserId;
    }
    public int getBorrowerUserId(){
        return borrowerUserId;
    }
    public long getMaxLoanAmount(){
        return maxLoanAmount;
    }
    public int getMonth(){
        return month;
    }

    // method used for getting all the details used for updating the loan details
    private void getLoanDistributionInfo(int distributionId){

        String query = "select loans.loan_id, balance_amount, lenders.lender_id, borrowers.borrower_id, loan_grant_amount, "
                    + " available_funds, current_loan_balance, lenders.user_id, borrowers.user_id, max_loan_amount, loan_tenure_months "
                    + " from loan_distribution inner join loans on loan_distribution.loan_id=loans.loan_id inner join lenders on "
                    + " lenders.lender_id=loan_distribution.lender_id inner join borrowers on borrowers.borrower_id = "
                    + " loan_distribution.borrower_id where distribution_id = ?"; 

        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setInt(1, getDistributionId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                setLoanId(resultSet.getInt(1));
                long balanceAmount = resultSet.getLong(2) - resultSet.getLong(5);
                String status;
                //new condition added, but it may not be neccessary because in LoanDetailAction we filter that in loanStatusFromLender()
                if (balanceAmount < 0) {
                    System.out.println("Loan cannot be accepted");
                    return;
                }
                if (balanceAmount == 0) {
                    status = "Fully Funded";
                }
                else{
                    status = "Partially Funded";
                }  

                setLenderId(resultSet.getInt(3));
                setBorrowerId(resultSet.getInt(4));
                setLoanGrantedAmount(resultSet.getLong(5));
                setAvailableFunds(resultSet.getLong(6));
                setCurrentBalance(resultSet.getLong(7));
                setLenderUserId(resultSet.getInt(8));
                setBorrowerUserId(resultSet.getInt(9));
                setMaxLoanAmount(resultSet.getLong(10));
                setMonth(resultSet.getInt(11));

                System.out.println("All the details are displayed from the tables");

                // checkCanAcceptLoan();
                acceptLoan(status,balanceAmount);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method to check if the particular loan can be accepted or not
    // private boolean checkCanAcceptLoan(){
    //     String query = "";
    //     try (
    //         Connection conn = db.getConnection();
    //         PreparedStatement preparedStatement = conn.prepareStatement(query);
    //     ) {
    //         return true;
    //     } 
    //     catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return false;
    // }

    // accepting the loan that the borrower selected
    private void acceptLoan(String status, long balanceAmount){

        String query = "update loans set status = ?, balance_amount = ?, updated_at = CURRENT_TIMESTAMP where loan_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, status);
            preparedStatement.setLong(2, balanceAmount);
            preparedStatement.setInt(3, getLoanId());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Loans table status has been updated..");
                updateLenderAmount();
                updateLoanBalance();
                updateLoanDistribution();
                updateDueDate();
                runScheduler();
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for activating mysql event scheduler that adds the emi every 1 month into the table
    private void runScheduler() {

        String eventName = "emi_for_loan_" + getLoanId() + "_with_lender_" + getLenderId();
        int loanId = getLoanId();
        int distId = getDistributionId();
    
        String endTimeQuery = "select DATE_SUB(loan_due_date, INTERVAL 1 MONTH) AS end_time FROM loans WHERE loan_id = " + loanId;
    
        try (
            Connection conn = db.getConnection();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(endTimeQuery);
        ) {
            if (rs.next()) {
                String endTime = rs.getString("end_time");
    
                String query = "CREATE EVENT " + eventName + " "
                        + "ON SCHEDULE "
                        + "EVERY 1 MONTH "
                        + "STARTS CURRENT_TIMESTAMP "
                        + "ENDS TIMESTAMP('" + endTime + "') "
                        + "DO "
                        + "BEGIN "
                        + "    INSERT INTO EMIs (loan_id, emi_amount) "
                        + "    SELECT loan_id, emi FROM loan_distribution WHERE distribution_id = " + distId + "; "
                        + "    UPDATE EMIs "
                        + "    SET due_date = DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH) "
                        + "    WHERE due_date IS NULL; "
                        + "END;";
    
                statement.executeUpdate(query);
                System.out.println("Scheduler has been set successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    // updating the lender table's fund amount that is deduct from it
    private void updateLenderAmount(){
        String query = "update lenders set available_funds = ? where lender_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            long remainingFund = getAvailableFunds() - getLoanGrantedAmount();

            preparedStatement.setLong(1, remainingFund);
            preparedStatement.setInt(2, getLenderId());
            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Lenders amount value has been updated..");
                updateTransactionHistory("lender", getLenderUserId());
                
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    // update the borrower's table's current loan balance amount (i.e) it will increase 
    private void updateLoanBalance(){
        String query = "update borrowers set current_loan_balance = ?, max_loan_amount = ? where borrower_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            
            long amount = getCurrentBalance() + getLoanGrantedAmount();
            long maxAmount = getMaxLoanAmount() - getLoanGrantedAmount();

            preparedStatement.setLong(1, amount);
            preparedStatement.setLong(2, maxAmount);
            preparedStatement.setInt(3, getBorrowerId());
            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Borrowers current loan balance is updated..");
                updateTransactionHistory("borrower", getBorrowerUserId());
                //insertEMI();
            }

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLoanDistribution(){
        String query = "update loan_distribution set is_loan_accepted=1, remaining_principle_amount=?, emi=?, remaining_months=? "
                    +" where distribution_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setLong(1, getLoanGrantedAmount());
            preparedStatement.setInt(2, getEmi());
            preparedStatement.setInt(3, getMonth());
            preparedStatement.setInt(4, getDistributionId());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Loan distribution table updated..");
            }
            
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // update transaction history for the lender
    private void updateTransactionHistory(String role,int id){

        String query = "insert into transaction_history (user_id, lender_id, borrower_id, amount, transaction_type) values (?,?,?,?,?)";

        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, getLenderId());
            preparedStatement.setInt(3, getBorrowerId());
            preparedStatement.setLong(4, getLoanGrantedAmount());

            if (role.equals("lender")) {
                preparedStatement.setString(5, "Lended");
            }
            else{
                preparedStatement.setString(5, "Borrowed");
            }
            
            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Transaction table is updated.. ");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for updating the loan due date 
    private void updateDueDate(){
        String query = "update loans set loan_due_date = DATE_ADD(CURRENT_TIMESTAMP, INTERVAL loan_tenure_months MONTH) where loan_id=? and "
                        +" loan_due_date IS NULL";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getLoanId());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Due date for the loan successfully fixed..");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for rejecting the loan
    public String rejectLoan(){
        String query = "delete from loan_distribution where distribution_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getDistributionId());

            int result = preparedStatement.executeUpdate();
            
            return result > 0 ? "success" : "error" ;
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    @Override
    public String execute(){
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("userSession");
            if (user != null && user.getUsername() != null) {
                getLoanDistributionInfo(getDistributionId());
                return "success";
            }
        }
        return "error";
    }
}
