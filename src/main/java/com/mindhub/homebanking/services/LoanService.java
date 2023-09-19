package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    Loan findById(Long id);
    boolean existsByName(String name);

    List<LoanDTO> getLoansDTO();

    void saveLoan(Loan loan);

}
