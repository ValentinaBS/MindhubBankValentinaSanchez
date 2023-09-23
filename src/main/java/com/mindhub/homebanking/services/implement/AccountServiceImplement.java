package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AccountServiceImplement implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientService clientService;


    @Override
    public boolean existsByNumber(String number) {
        return accountRepository.existsByNumber(number);
    }

    @Override
    public boolean existsByIdAndClient_Id(Long id, Long clientId) {
        return accountRepository.existsByIdAndClient_Id(id, clientId);
    }

    @Override
    public long countByActiveAndClient_Id(Boolean active, long clientId) {
        return accountRepository.countByActiveAndClient_Id(active, clientId);
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number);
    }

    @Override
    public List<Account> findByClient(Client client) {
        return accountRepository.findByClient(client);
    }

    @Override
    public List<AccountDTO> getAccountsDTO() {
        return accountRepository
                .findAll()
                .stream()
                .map(AccountDTO::new)
                .collect(toList());
    }

    @Override
    public List<AccountDTO> getClientCurrentAccountsDTO(String email) {
        return  accountRepository
                .findByClient(clientService.findByEmail(email))
                .stream()
                .map(AccountDTO::new)
                .collect(toList());
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }
}
