package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Transaction;

import java.util.Set;

public interface TransactionService {

    Set<Transaction> findByAccount_Id(Long accountId);

    void saveTransaction(Transaction transaction);
}
