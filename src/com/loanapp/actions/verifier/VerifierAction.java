package com.loanapp.actions.verifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

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

    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> newUsers = new ArrayList<>();
    private ArrayList<User> blockedUsers = new ArrayList<>();

    @Override
    public User getModel() {
        return user;
    }

    //setters
    public void setUsers(ArrayList<User> users){
        this.users = users;
    }
    public void setNewUsers(ArrayList<User> newUsers){
        this.newUsers = newUsers;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setBlockedUsers(ArrayList<User> blockedUsers){
        this.blockedUsers = blockedUsers;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }

    //getters
    public ArrayList<User> getUsers(){
        return users;
    }
    public ArrayList<User> getNewUsers(){
        return newUsers;
    }
    public String getUsername(){
        return username;
    }
    public ArrayList<User> getBlockedUsers(){
        return blockedUsers;
    }
    public int getId(){
        getUserId();
        return userId;
    }

    //query to getting all the borrowes and lenders (users)
    private void getAllUsers(){
        String query = "select username, emailId, userType, status from users inner join verification on users.user_id=verification.user_id where userType in ('borrower', 'lender')";
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
                users.add(user);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //query for getting the new verifiers for verification process
    private void getAllNewUsers(){
        String query = "select username, emailId, userType, status from users inner join verification on users.user_id=verification.user_id where userType in ('borrower', 'lender') and status in ('In progress')";
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
        String query = "select username,emailId,userType from users where isBlocked=1";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

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
        String query = "select user_id from users where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setUserId(resultSet.getInt(1));
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method for accepting the user
    public String acceptUser(){ 
        String query = "update users set isVerified=1, randomSalt=?, password=?  where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            String pwd = passwordGenertor.generatePassword();

            // sending the password to the respective user email id
            sendMail(pwd,getUsername());

            String[] hashedPassword = passwordHasher.generateHashedPassword(pwd);

            preparedStatement.setString(1, hashedPassword[0]);
            preparedStatement.setString(2, hashedPassword[1]);
            preparedStatement.setString(3, getUsername());

            int value = preparedStatement.executeUpdate();

            if (value > 0) {
                updateVerificationTable("Verified");
                return "success";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }


    // sending email to the user with the password  
    private void sendMail(String password, String uname){
        String query = "select emailId from users where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {

            preparedStatement.setString(1, uname);
            ResultSet resultSet = preparedStatement.executeQuery();
        
            if (resultSet.next()) {
                String email = resultSet.getString("emailId");
                String subject = "You have been verified";
                String message =    "<body>" +
                                        "<p>Greetings, " + uname + "</p>" +
                                        "<p>You have been verified and can now use the application.</p>" +
                                        "<p><strong>Your password is: " + password + "</strong></p>" +
                                        "<p><a href='http://localhost:8080/loanApplication/login.jsp'>Click here to log in</a></p>" +
                                    "</body>" ;

                mail.sendMailThread(email, subject, message);
            }
            else{
                System.out.println("No user available.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //method for rejecting the user
    public String rejectUser(){
        String query = "update users set isVerified=0 where username=?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, getUsername());
            int value = preparedStatement.executeUpdate();
            if (value > 0) {
                updateVerificationTable("Rejected");
                return "success";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }


    // updating the entry into verification table
    private void updateVerificationTable(String status){

        String query = "update verification set verified_by=1 , status=? , verification_date=NOW() where user_id=?";

        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, getId());
            preparedStatement.executeUpdate();

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String execute(){
        getAllUsers();
        getAllNewUsers();
        getAllBlockedUsers();
        return "success";
    }
    
}
