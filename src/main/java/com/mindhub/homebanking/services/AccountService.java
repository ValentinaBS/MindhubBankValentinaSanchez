package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface AccountService {

    boolean existsByNumber(String number);
    boolean existsByIdAndClient_Id(Long id, Long clientId);
    Account findById(Long id);
    Account findByNumber(String number);
    List<Account> findByClient(Client client);

    List<AccountDTO> getAccountsDTO();
    List<AccountDTO> getClientCurrentAccountsDTO(String email);
    void saveAccount(Account account);
}
