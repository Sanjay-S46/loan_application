package com.loanapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private String url = "jdbc:mysql://localhost:3306/loan_application" ;
	private String uname = "sanjay" ;
	private String pwd = "sanjay" ;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url,uname,pwd);
        return conn;
    }

}
