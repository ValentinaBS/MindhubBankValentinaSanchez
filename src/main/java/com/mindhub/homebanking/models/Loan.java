package com.mindhub.homebanking.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Loan {

    // ---- Properties ----
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native") // Generates ID value automatically
    private long id;
    private String name;
    private double maxAmount;
    @ElementCollection
    @Column(name="payments")
    private List<Integer> payments = new ArrayList<>();

    // ---- Relations ----
    @OneToMany(mappedBy="loan", fetch= FetchType.EAGER)
    // How do we want to bring the data. EAGER brings Client with Account. LAZY brings Client only.
    private Set<ClientLoan> clientLoans = new HashSet<>();

    // ---- Constructors ----
    public Loan(){ }
    public Loan(String name, double maxAmount, List<Integer> payments) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
    }

    // ---- Getters & Setters ----
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getMaxAmount() {
        return maxAmount;
    }
    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }
    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }

    public void addClientLoan(ClientLoan clientLoan) {
        clientLoan.setLoan(this);
        clientLoans.add(clientLoan);
    }

    @JsonIgnore
    public List<Client> getClients() {
        return clientLoans
                .stream()
                .map(ClientLoan::getClient)
                .collect(toList());
    }
}
