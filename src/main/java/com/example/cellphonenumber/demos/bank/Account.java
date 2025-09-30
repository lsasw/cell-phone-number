package com.example.cellphonenumber.demos.bank;

public class Account {
    private final String id; // 账户ID（唯一标识）
    // 账户余额
    private double balance;

    public Account(String id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() { return id; }
    public double getBalance() { return balance; }
    
    // 仅允许通过TransferService修改余额，避免直接外部修改
    protected void withdraw(double amount) { balance -= amount; }
    protected void deposit(double amount) { balance += amount; }
}