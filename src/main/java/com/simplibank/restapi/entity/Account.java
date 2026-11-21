package com.simplibank.restapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_holder", nullable = false)
    private String accountHolder;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "pin_hash")
    private String pinHash;

    protected Account() { }   // JPA needs an empty constructor

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public BigDecimal getBalance() { return balance; }
    public String getPinHash() { return pinHash; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}