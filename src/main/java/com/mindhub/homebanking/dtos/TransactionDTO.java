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

    // ---- Constructors ----
    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.transferDate = transaction.getTransferDate();
        this.type = transaction.getType();
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
}
