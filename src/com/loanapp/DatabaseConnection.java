package com.loanapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static final String url = "jdbc:mysql://localhost:3306/loan_application" ;
	private static final String uname = "sanjay" ;
	private static final String pwd = "sanjay" ;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url,uname,pwd);
        return conn;
    }

}
