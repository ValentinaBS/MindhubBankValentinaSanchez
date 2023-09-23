package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Loan;

import java.util.ArrayList;
import java.util.List;

public class LoanDTO {

    // ---- Properties ----
    private long id;
    private String name;
    private Double maxAmount;
    private List<Integer> payments;
    private Double percentage;

    // ---- Constructor ----

    // To receive, empty constructor. Create another dto.
    public LoanDTO() {}
    public LoanDTO(Loan loan) {
        this.id = loan.getId();
        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payments = loan.getPayments();
        this.percentage = loan.getPercentage();
    }

    // ---- Getters ----
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public Double getPercentage() {
        return percentage;
    }
}
