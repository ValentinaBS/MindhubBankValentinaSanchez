package com.mindhub.homebanking.repositories;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNumber(String number);

    Account findByNumber(String number);

    List<Account> findByClient(Client client);

    boolean existsByIdAndClient_Id(long id, long clientId);
} // Creates a repository to save the accounts in the database
