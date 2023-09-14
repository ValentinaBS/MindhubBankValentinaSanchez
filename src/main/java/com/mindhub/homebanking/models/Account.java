package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity // Creates an empty table
public class Account {

    // ---- Properties ----
    @Id // Indicates Primary Key
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native") // Generates ID value automatically
    private long id;
    private String number;
    private LocalDate creationDate;
    private Double balance;
    private Boolean active;

    // ---- Relations ----
    @ManyToOne(fetch = FetchType.EAGER)
    // How do we want to bring the data. EAGER brings Account with the linked Client. LAZY brings Account only.
    @JoinColumn(name="client_id")
    // Adds column in Account table, with the Foreign Key using the Client Primary Key
    private Client client; // Property of the Account class

    // Transactions relation
    @OneToMany(mappedBy="account", fetch= FetchType.EAGER)
    private Set<Transaction> transactions = new HashSet<>();

    // ---- Constructors ----
    public Account(){} // It's used to map by Hibernate. DTOs don't because they don´t persist.
    public Account(String number, LocalDate creationDate, Double balance, Boolean active) {
        this.number = number;
        this.creationDate = creationDate;
        this.balance = balance;
        this.active = active;
    }

    // ---- Getters & Setters ----
    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Double getBalance() {
        return balance;
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        transaction.setAccount(this);
        transactions.add(transaction);
    }
}
