package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity // Creates an empty table
public class Transaction {

    // ---- Properties ----
    @Id // Indicates Primary Key
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native") // Generates ID value automatically
    private long id;
    private Double amount;
    private String description;
    private LocalDateTime transferDate;
    private TransactionType type;

    // ---- Relations ----
    @ManyToOne(fetch = FetchType.EAGER)
    // How do we want to bring the data. EAGER brings Transaction with the linked Account. LAZY brings Transaction only.
    @JoinColumn(name="account_id")
    // Adds column in Transaction table, with the Foreign Key using the Account Primary Key
    private Account account;

    // ---- Constructors ----
    public Transaction(){} // It's used to map by Hibernate. DTOs don't because they don´t persist.
    public Transaction(Double amount, String description, LocalDateTime transferDate, TransactionType type) {
        this.amount = amount;
        this.description = description;
        this.transferDate = transferDate;
        this.type = type;
    }

    // ---- Getters & Setters ----
    public long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }
    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }

    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
}
