package com.mindhub.homebanking.dtos;

import java.util.List;

public class LoanApplicationDTO {

    // ---- Properties ----
    private long id;
    private Double amount;
    private Integer payments;
    private String destinataryAccountNumber;

    // ---- Constructor ----
    public LoanApplicationDTO(long id, Double amount, Integer payments, String destinataryAccountNumber) {
        this.id = id;
        this.amount = amount;
        this.payments = payments;
        this.destinataryAccountNumber = destinataryAccountNumber;
    }

    // ---- Getters ----
    public long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public String getDestinataryAccountNumber() {
        return destinataryAccountNumber;
    }
}
