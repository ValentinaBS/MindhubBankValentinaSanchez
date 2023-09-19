package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LoanServiceImplement implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Override
    public Loan findById(Long id) {
        return loanRepository.findById(id).orElse(null);
    }

    @Override
    public boolean existsByName(String name) {
        return loanRepository.existsByName(name);
    }

    @Override
    public List<LoanDTO> getLoansDTO() {
        return loanRepository
                .findAll()
                .stream()
                .map(LoanDTO::new)
                .collect(toList());
    }

    @Override
    public void saveLoan(Loan loan) {
        loanRepository.save(loan);
    }
}
