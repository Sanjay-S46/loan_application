package com.loanapp.models;

public class Loan {
    
    private int userId;
    private long loanAmount;
    private String status;
    private int loanMonth;
    private String loanType;
    private String loanPurpose;
    private long maxLoanAmount;
    private int loanId;

    // setters
    public void setLoanAmount(long loanAmount){
        this.loanAmount = loanAmount;
    }
    public void setLoanMonth(int loanMonth){
        this.loanMonth = loanMonth;
    }
    public void setLoanType(String loanType){
        this.loanType = loanType;
    }
    public void setLoanPurpose(String loanPurpose){
        this.loanPurpose = loanPurpose;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public void setMaxLoanAmount(long maxLoanAmount){
        this.maxLoanAmount = maxLoanAmount;
    }
    public void setLoanId(int loanId){
        this.loanId = loanId;
    }

    // getters
    public long getLoanAmount(){
        return loanAmount;
    }
    public int getLoanMonth(){
        return loanMonth;
    }
    public String getLoanType(){
        return loanType;
    }
    public String getLoanPurpose(){
        return loanPurpose;
    }
    public int getUserId(){
        return userId;
    }
    public String getStatus(){
        return status;
    }
    public long getMaxLoanAmount(){
        return maxLoanAmount;
    }
    public int getLoanId(){
        return loanId;
    }

}
