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

    //query to getting all the borrowes and lenders (users)
    private void getAllUsers(){
        String query = "select * from users where isVerified=1 and isBlocked=0 and userType in ('borrower', 'lender')";
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
                
                //setting the database values to the setter methods of ther User class
                user.setUsername(username);
                user.setEmailId(emailId);
                user.setUserType(userType);

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
        String query = "select * from users where userType in ('borrower', 'lender') and isVerified=0";
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
                
                //setting the database values to the setter methods of ther User class
                user.setUsername(username);
                user.setEmailId(emailId);
                user.setUserType(userType);

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
        String query = "select * from users where isBlocked=1";
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
    
                // if (mail.sendMail(email, subject, message)) {
                //     System.out.println("Mail sent successfully");
                // }
                // else{
                //     System.out.println("Mail failed to send");
                // }

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
        String query = "delete from users where username = ?";
        try (
            Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setString(1, getUsername());
            int value = preparedStatement.executeUpdate();
            if (value > 0) {
                return "success";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }


    @Override
    public String execute(){
        getAllUsers();
        getAllNewUsers();
        getAllBlockedUsers();
        return "success";
    }
    
}
