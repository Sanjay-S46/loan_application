package com.loanapp.models;

import java.sql.Date;

public class TransactionHistory{

    private long amount;
    private Date date;
    private String transactionType;
    private String name;

    // setters
    public void setAmount(long amount){
        this.amount = amount;
    }
    public void setDate(Date date){
        this.date = date;
    }
    public void setTransactionType(String transactionType){
        this.transactionType = transactionType;
    }
    public void setName(String name){
        this.name = name;
    }

    // getters  
    public long getAmount(){
        return amount;
    }
    public Date getDate(){
        return date;
    }
    public String getTransactionType(){
        return transactionType;
    }
    public String getName(){
        return name;
    }

}