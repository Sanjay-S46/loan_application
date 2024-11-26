package com.loanapp.actions.borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.Loan;
import com.loanapp.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class LoanRequestAction extends ActionSupport implements ModelDriven<Loan>{

    private DatabaseConnection db = new DatabaseConnection();
    private Loan loan = new Loan();

    @Override
    public Loan getModel() {
        return loan;
    }

    // method to check if the applying loan amount is less the maximum
    // amount allocated for the borrower

    private boolean checkForLoanRequest(int userId){
        String query = "select max_loan_amount from borrowers where user_id = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                long maxAmount = resultSet.getLong(1);
                if (loan.getLoanAmount() < maxAmount) {
                    return true;
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // applying loan form
    private void applyForLoan(int userId){
        String query = "insert into loans (borrower_id, requested_amount, loan_type, loan_purpose, loan_tenure_months,balance_amount) "
                    + "values ( (select borrower_id from borrowers where user_id = ?) , ?, ? ,?, ?, ?)";

        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            
            preparedStatement.setInt(1, userId);
            preparedStatement.setLong(2, loan.getLoanAmount());
            preparedStatement.setString(3, loan.getLoanType());
            preparedStatement.setString(4, loan.getLoanPurpose());
            preparedStatement.setInt(5, loan.getLoanMonth());
            preparedStatement.setLong(6, loan.getLoanAmount());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Loan successfully applied...");
            }
            else{
                System.out.println("Loan request failed..");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String execute(){

        HttpSession session =  ServletActionContext.getRequest().getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("userSession");
            int userId = user.getUserId();
            if (checkForLoanRequest(userId)) {
                applyForLoan(userId);
                return "success";
            }
        }
        return "error";

    }
    

}
