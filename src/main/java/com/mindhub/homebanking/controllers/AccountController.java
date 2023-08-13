package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
@RequestMapping("/api")
public class AccountController {

    @Autowired
    // Injects ClientRepository to use it in this controller
    public AccountRepository accountRepository;
    // Interface. We do this to use methods with accountRepository.

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountRepository
                .findAll()
                .stream()
                .map(AccountDTO::new)
                .collect(toList());
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable long id){
        return new AccountDTO(accountRepository.findById(id).orElse(null));
    }
}
