package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;

public class ClientLoanDTO {

    // ---- Properties ----
    private long id;
    private long loanId;
    private String name;
    private double amount;
    private int payments;
    private int paymentsLeft;
    private boolean active;

    // ---- Constructors ----
    public ClientLoanDTO(ClientLoan clientLoan) {

        this.id = clientLoan.getId();

        this.loanId = clientLoan.getLoan().getId();

        this.name = clientLoan.getLoan().getName();

        this.amount = clientLoan.getAmount();

        this.payments = clientLoan.getPayments();

        this.paymentsLeft = clientLoan.getPaymentsLeft();

        this.active = clientLoan.getActive();

    }

    // ---- Getters ----

    public long getId() {
        return id;
    }

    public long getLoanId() {
        return loanId;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public int getPayments() {
        return payments;
    }

    public int getPaymentsLeft() {
        return paymentsLeft;
    }

    public boolean isActive() {
        return active;
    }
}
