package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;

import java.time.LocalDateTime;

public class TransactionDTO {

    // ---- Properties ----
    private long id;
    private Double amount;
    private String description;
    private LocalDateTime transferDate;
    private TransactionType type;
    private Double currentBalance;
    private Boolean active;

    // ---- Constructors ----
    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.transferDate = transaction.getTransferDate();
        this.type = transaction.getType();
        this.currentBalance = transaction.getCurrentBalance();
        this.active = transaction.getActive();
    }

    // ---- Getters ----
    public long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public TransactionType getType() {
        return type;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public Boolean getActive() {
        return active;
    }
}
