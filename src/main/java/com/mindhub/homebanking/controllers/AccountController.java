package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.mindhub.homebanking.utils.AccountUtils.getRandomAccountNumber;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountService.getAccountsDTO();
    }

    @GetMapping("clients/current/accounts")
    public List<AccountDTO> getClientCurrentAccounts(Authentication authentication) {
        return accountService.getClientCurrentAccountsDTO(authentication.getName());
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication) {

        Client authClient = clientService.findByEmail(authentication.getName());

        if (authClient != null) {
            if(authClient.getAccounts().size() >= 3){
                return new ResponseEntity<>("You can't create more than 3 accounts.", HttpStatus.FORBIDDEN);
            }

            String formattedAccountNumber = getRandomAccountNumber(accountService);

            Account newAccount = new Account(formattedAccountNumber, LocalDate.now(), 0.0);
            authClient.addAccount(newAccount);
            accountService.saveAccount(newAccount);

            return new ResponseEntity<>("Account has been created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Unknown user", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable long id, Authentication authentication){

        Client currentClient = clientService.findByEmail(authentication.getName());
        Account account = accountService.findById(id);

        if(account != null && accountService.existsByIdAndClient_Id(id, currentClient.getId())) {
            return new ResponseEntity<>(new AccountDTO(account), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
        }
    }
}
