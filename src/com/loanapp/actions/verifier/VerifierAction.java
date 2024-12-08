package com.loanapp.actions.verifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.loanapp.DatabaseConnection;
import com.loanapp.models.User;
import com.loanapp.utils.GeneratePassword;
import com.loanapp.utils.HashPassword;
import com.loanapp.utils.SendMail;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class VerifierAction extends ActionSupport implements ModelDriven<User>{

    private DatabaseConnection db = new DatabaseConnection();
    private GeneratePassword passwordGenertor = new GeneratePassword();
    private HashPassword passwordHasher = new HashPassword();
    private SendMail mail = new SendMail();

    private User user;
    private String username;
    private int userId;
    private String userType;
    private int verifierId;

    private ArrayList<User> borrowers = new ArrayList<>();
    private ArrayList<User> lenders = new ArrayList<>();
    private ArrayList<User> newUsers = new ArrayList<>();
    private ArrayList<User> blockedUsers = new ArrayList<>();
    private ArrayList<User> rejectedUsers = new ArrayList<>();
    

    @Override
    public User getModel() {
        return user;
    }

    //setters
    public void setBorrowers(ArrayList<User> borrowers){
        this.borrowers = borrowers;
    }
    public void setLenders(ArrayList<User> lenders){
        this.lenders = lenders;
    }
    public void setRejectedUsers(ArrayList<User> rejectedUsers){
        this.rejectedUsers = rejectedUsers;
    }
    public void setNewUsers(ArrayList<User> newUsers){
        this.newUsers = newUsers;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setUserType(String userType){
        this.userType = userType;
    }
    public void setBlockedUsers(ArrayList<User> blockedUsers){
        this.blockedUsers = blockedUsers;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }
    public void setVerifierId(int verifierId){
        this.verifierId = verifierId;
    }

    //getters
    public ArrayList<User> getBorrowers(){
        return borrowers;
    }
    public ArrayList<User> getLenders(){
        return lenders;
    }
    public ArrayList<User> getRejectedUsers(){
        return rejectedUsers;
    }
    public ArrayList<User> getNewUsers(){
        return newUsers;
    }
    public String getUsername(){
        return username;
    }
    public String getUserType(){
        return userType;
    }
    public ArrayList<User> getBlockedUsers(){
        return blockedUsers;
    }
    public int getId(){
        getUserId();
        return userId;
    }
    public int getVerifierId(){
        return verifierId;
    }

    //query to getting all the borrowes and lenders (users)
    private void getAllUsers(){
        String query = "select username, emailId, userType, status from users inner join verification on users.user_id = "
                    + " verification.user_id where userType in ('borrower','lender') and isBlocked=0 and isVerified=1 and verified_by=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getVerifierId());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user = new User();
                
                //getting the values from the database
                String username = resultSet.getString("username");
                String emailId = resultSet.getString("emailId");
                String userType = resultSet.getString("userType");
                String status = resultSet.getString("status");
                
                //setting the database values to the setter methods of ther User class
                user.setUsername(username);
                user.setEmailId(emailId);
                user.setUserType(userType);
                user.setStatus(status);

                //adding the user to the arraylist
                if (status.equals("Rejected")) {
                    rejectedUsers.add(user);
                }
                else if (userType.equals("borrower")) {
                    borrowers.add(user);
                }
                else if (userType.equals("lender")){
                    lenders.add(user);
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //query for getting the new verifiers for verification process
    private void getAllNewUsers(){
        String query = "select username, emailId, userType, status from users inner join verification on users.user_id = "
                    + " verification.user_id where userType in ('borrower', 'lender') and status in ('In progress')";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user = new User();
                
                //getting the values from the database
                String username = resultSet.getString("username");
                String emailId = resultSet.getString("emailId");
                String userType = resultSet.getString("userType");
                String status = resultSet.getString("status");
                
                //setting the database values to the setter methods of ther User class
                user.setUsername(username);
                user.setEmailId(emailId);
                user.setUserType(userType);
                user.setStatus(status);

                //adding the user to the arraylist
                newUsers.add(user);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for getting all the blocked users
    public void getAllBlockedUsers(){
        String query = "select username,emailId,userType from users inner join verification on users.user_id = verification.user_id "
                    + " where isBlocked=1 and verified_by = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getVerifierId());
            
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user = new User();
                
                //getting the values from the database
                String username = resultSet.getString(1);
                String emailId = resultSet.getString(2);
                String userType = resultSet.getString(3);
                
                //setting the database values to the setter methods of ther User class
                user.setUsername(username);
                user.setEmailId(emailId);
                user.setUserType(userType);

                //adding the user to the arraylist
                blockedUsers.add(user);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method for getting the user id
    private void getUserId(){
        String query = "select user_id,userType from users where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setUserId(resultSet.getInt(1));
                setUserType(resultSet.getString(2)+"s");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method for accepting the user
    public String acceptUser() throws ClassNotFoundException, SQLException{ 
        
        String selectQuery = "select emailId from users where username = ? for update " ; 
        String updateQuery = "update users set isVerified=1, randomSalt=?, password=?  where username=?";
        String insertQuery = "INSERT INTO " + getUserType() + " (user_id) SELECT user_id FROM users WHERE username = ?";

        String username = getUsername();
         
        Connection conn = db.getConnection();
        
        try ( 
            PreparedStatement selectPrepStmt = conn.prepareStatement(selectQuery);
            PreparedStatement updatePrepStmt = conn.prepareStatement(updateQuery);
            PreparedStatement insertPrepStmt = conn.prepareStatement(insertQuery);
        ) {
            conn.setAutoCommit(false);

            selectPrepStmt.setString(1, username);

            ResultSet resultSet = selectPrepStmt.executeQuery();
            String email;

            if (resultSet.next()) {
                email = resultSet.getString(1);
            }
            else{
                conn.rollback();
                return "error"; 
            }

            String pwd = passwordGenertor.generatePassword();
            String[] hashedPassword = passwordHasher.generateHashedPassword(pwd);

            updatePrepStmt.setString(1, hashedPassword[0]);
            updatePrepStmt.setString(2, hashedPassword[1]);
            updatePrepStmt.setString(3, username);

            int value = updatePrepStmt.executeUpdate();

            if (value > 0 && email != null) {
                // sending the password to the respective user email id
                sendMail(pwd,username,email);
                boolean isUpdated = updateVerificationTable(conn, "Verified");

                if (!isUpdated) {
                    throw new Exception();
                }

                // after accepting the particular user (lender or borrower) adding to the respective table
                insertPrepStmt.setString(1, username);
                insertPrepStmt.executeUpdate();

                System.out.println("Successfully inserted into the " + getUserType() +" table ..");
                
                conn.commit();
                return "success";
            }
            
        } 
        catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        }
        finally{
            conn.setAutoCommit(true);
            conn.close();
        }
        return "error";
    }


    // sending email to the user with the password  
    private void sendMail(String password, String uname, String email) throws InterruptedException {
        String subject = "You have been verified";
        String message =    "<body>" +
                                "<p>Greetings, " + uname + "</p>" +
                                "<p>You have been verified and can now use the application.</p>" +
                                "<p><strong>Your password is: " + password + "</strong></p>" +
                                "<p><a href='http://localhost:8080/loanApplication/login.jsp'>Click here to log in</a></p>" +
                            "</body>" ;
        Thread.sleep(300);
        mail.sendMailThread(email, subject, message);
    }


    //method for rejecting the user
    public String rejectUser() throws ClassNotFoundException, SQLException{

        String selectQuery = "select user_id from users where username = ? for update";
        String updateQuery = "update users set isVerified=0 where username=?";
        String username = getUsername();

        Connection conn = db.getConnection();

        try (
            PreparedStatement selectPrepStmt = conn.prepareStatement(selectQuery);
            PreparedStatement updatePrepStmt = conn.prepareStatement(updateQuery);
        ) {
            conn.setAutoCommit(false);

            selectPrepStmt.setString(1, username);

            ResultSet resultSet = selectPrepStmt.executeQuery();
            if (!resultSet.next()) {
                conn.rollback();
                return "error";
            }

            updatePrepStmt.setString(1, username);
            int value = updatePrepStmt.executeUpdate();

            if (value > 0) {
                boolean isUpdated = updateVerificationTable(conn, "Rejected");

                if (!isUpdated) {
                    throw new Exception();
                }
    
                conn.commit();
                return "success";
            }

        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        }
        finally{
            conn.setAutoCommit(true);
            conn.close();
        }
        return "error";
    }


    // updating the entry into verification table
    private boolean updateVerificationTable(Connection conn, String status){

        String query = "update verification set verified_by=? , status=? , verification_date=NOW() where user_id=?";

        try (
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, getVerifierId());
            preparedStatement.setString(2, status);
            preparedStatement.setInt(3, getId());
            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                return true;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public String execute(){
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        if (session != null) {
            User sessionUser = (User) session.getAttribute("userSession");
            if (sessionUser != null) {

                setVerifierId(sessionUser.getUserId());

                getAllUsers();
                getAllNewUsers();
                getAllBlockedUsers();
                return "success";
            }
        }
        return "error";
    }
    
}
