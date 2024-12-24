package com.example.cashbook;

public class Transaction {
    private String user_Id;
    private String remark;
    private String date;
    private String transaction_Type;
    private String node_Id;
    private long income,expense,balance;


    public String getNode_Id() {
        return node_Id;
    }

    public void setNode_Id(String node_Id) {
        this.node_Id = node_Id;
    }


    public Transaction(){}

    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTransaction_Type() {
        return transaction_Type;
    }

    public void setTransaction_Type(String transaction_Type) {
        this.transaction_Type = transaction_Type;
    }

    public long getIncome() {
        return income;
    }

    public void setIncome(long income) {
        this.income = income;
    }

    public long getExpense() {
        return expense;
    }

    public void setExpense(long expense) {
        this.expense = expense;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
