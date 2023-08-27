package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
@RequestMapping("/api")
public class AccountController {

    @Autowired
    // Injects AccountRepository to use it in this controller
    private AccountRepository accountRepository;
    // Interface. We do this to use methods with accountRepository.
    @Autowired
    private ClientRepository clientRepository;

    private String getRandomAccountNumber() {
        String formattedAccountNumber;
        do {
            // Generates a random number between 0 (inclusive) and 99999999 (exclusive)
            int randomNumber = new Random().nextInt(100000000);
            // Ensures that the output will always be in an 8-digit format
            formattedAccountNumber = "VIN-" + String.format("%08d", randomNumber);
        } while (accountRepository.existsByNumber(formattedAccountNumber)); // Avoids repeated account numbers
        return formattedAccountNumber;
    }

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountRepository
                .findAll()
                .stream()
                .map(AccountDTO::new)
                .collect(toList());
    }

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(Authentication authentication) {

        Client authClient = clientRepository.findByEmail(authentication.getName());

        if(authClient.getAccounts().size() >= 3){
            return new ResponseEntity<>("You can't create more than 3 accounts.", HttpStatus.FORBIDDEN);
        }

        String formattedAccountNumber = getRandomAccountNumber();

        Account newAccount = new Account(formattedAccountNumber, LocalDate.now(), 0.0);
        authClient.addAccount(newAccount);
        accountRepository.save(newAccount);

        return new ResponseEntity<>("Account has been created successfully", HttpStatus.CREATED);
    }

    @RequestMapping("/accounts/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable long id, Authentication authentication){

        Client currentClient = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findById(id).orElse(null);

        if(currentClient.getId() == account.getClient().getId()) {
            return new ResponseEntity<>(new AccountDTO(account), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
    }
}
