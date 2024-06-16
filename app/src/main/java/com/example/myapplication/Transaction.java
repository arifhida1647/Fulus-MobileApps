package com.example.myapplication;
public class Transaction {
    private String category;
    private String amount;
    private String userName;
    private String date;

    public Transaction(String category, String amount, String userName, String date) {
        this.category = category;
        this.amount = amount;
        this.userName = userName;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
