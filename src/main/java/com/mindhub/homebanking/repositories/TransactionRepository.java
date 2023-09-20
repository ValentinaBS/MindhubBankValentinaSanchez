package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RepositoryRestResource
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Set<Transaction> findByAccount_Id(Long accountId);

    List<Transaction> findByTransferDateBetweenAndAccount_Number(LocalDateTime dateInit, LocalDateTime dateEnd, String accountNumber);

} // Creates a repository to save the transactions in the database
