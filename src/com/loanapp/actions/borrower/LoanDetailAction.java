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

class LoanDetail{

    private String lender;
    private int interestRate;
    private long loanGrantedAmount;
    private int months;
    private int emi;
    private int loanId;
    private int distributionId;

    //setters   
    public void setLender(String lender){
        this.lender = lender;
    }
    public void setInterestRate(int interestRate){
        this.interestRate = interestRate;
    }
    public void setLoanGrantedAmount(long loanGrantedAmount){
        this.loanGrantedAmount = loanGrantedAmount;
    }
    public void setMonths(int months){
        this.months = months;
    }
    public void setEmi(int emi){
        this.emi = emi;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }
    public void setDistributionId(int distributionId){
        this.distributionId = distributionId;
    }

    //getters
    public String getLender(){
        return lender;
    }
    public int getInterestRate(){
        return interestRate;
    }
    public long getLoanGrantedAmount(){
        return loanGrantedAmount;
    }
    public int getMonths(){
        return months;
    }
    public int getEmi(){
        return emi;
    }
    public int getLoanId(){
        return loanId;
    }
    public int getDistributionId(){
        return distributionId;
    }

}

// ===============================================================================================================================

public class LoanDetailAction extends ActionSupport{

    private DatabaseConnection db = new DatabaseConnection();
    private LoanDetail loanDetail;
    private ArrayList<LoanDetail> loanDetails = new ArrayList<>();

    private int loanId;

    // setters
    public void setLoanDetails(ArrayList<LoanDetail> loanDetails){
        this.loanDetails = loanDetails;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }

    // getters
    public ArrayList<LoanDetail> getLoanDetails(){
        return loanDetails;
    }
    public int getLoanId(){
        return loanId;
    }

    public String getLenderName(int lenderId){
        String query = "select username from users where user_id = (select user_id from lenders where lender_id = ?)";
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
    


    // method for getting the loan details from lender
    public void loanStatusFromLender(){
        String query = "select lender_id, interest_rate, loan_grant_amount, loan_tenure_months, loans.loan_id, distribution_id from "
                    + " loan_distribution inner join loans on loan_distribution.loan_id = loans.loan_id where loans.loan_id = ? and "
                    +" status not in ('Fully Funded','Closed') and is_loan_accepted=0 ";

        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setInt(1, getLoanId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                loanDetail = new LoanDetail();

                int interest = resultSet.getInt(2);
                long amount = resultSet.getLong(3);
                int months = resultSet.getInt(4);
                int emi = calculateEMI(amount, interest, months);
                
                loanDetail.setLender(getLenderName(resultSet.getInt(1)));
                loanDetail.setInterestRate(interest);
                loanDetail.setLoanGrantedAmount(amount);
                loanDetail.setMonths(months);
                loanDetail.setEmi(emi);
                loanDetail.setLoanId(resultSet.getInt(5));
                loanDetail.setDistributionId(resultSet.getInt(6));

                loanDetails.add(loanDetail);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // calculate the emi amount for the loan
    private int calculateEMI(long amount, int interestRate,int months){

        double principal = (double) amount;
        double interest = (double) interestRate;
        double monthlyInterestRate = interest/12/100;

        double value = Math.pow(1+monthlyInterestRate,months);
        double emi = principal * monthlyInterestRate * value / (value-1);
        return (int)Math.ceil(emi); 
    }
    
    @Override
    public String execute(){
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        if (session != null) {
            User sessionUser = (User) session.getAttribute("userSession");
            if (sessionUser!=null && sessionUser.getUsername() != null) {

                loanStatusFromLender();

                return "success";
            }
            else{
                System.out.println("Username not available");
            }
        }
        return "error";
    }
}
