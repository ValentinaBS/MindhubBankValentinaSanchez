package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class TransactionServiceImplement implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    public Set<Transaction> findByAccount_Id(Long accountId) {
        return transactionRepository.findByAccount_Id(accountId);
    }

    @Override
    public List<Transaction> findByTransferDateBetweenAndActiveAndAccount_Number(LocalDateTime dateInit, LocalDateTime dateEnd, Boolean active, String accountNumber) {
        return transactionRepository.findByTransferDateBetweenAndActiveAndAccount_Number(dateInit, dateEnd, active, accountNumber);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
