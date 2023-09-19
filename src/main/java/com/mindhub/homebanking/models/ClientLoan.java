package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class ClientLoan {

    // ---- Properties ----
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native") // Generates ID value automatically
    private long id;
    private double amount;
    private int payments;
    private int paymentsLeft;
    private boolean active;

    // ---- Relations ----
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="loan_id")
    private Loan loan;

    // ---- Constructors ----
    public  ClientLoan() { }
    public ClientLoan(double amount, int payments, int paymentsLeft, boolean active) {
        this.amount = amount;
        this.payments = payments;
        this.paymentsLeft = paymentsLeft;
        this.active = active;
    }

    // ---- Getters & Setters ----

    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPayments() {
        return payments;
    }
    public void setPayments(Integer payments) {
        this.payments = payments;
    }

    public int getPaymentsLeft() {
        return paymentsLeft;
    }
    public void setPaymentsLeft(int paymentsLeft) {
        this.paymentsLeft = paymentsLeft;
    }

    public boolean getActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public Loan getLoan() {
        return loan;
    }
    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}
