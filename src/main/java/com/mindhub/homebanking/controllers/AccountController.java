package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.mindhub.homebanking.utils.AccountUtils.getRandomAccountNumber;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountService.getAccountsDTO();
    }

    @GetMapping("clients/current/accounts")
    public List<AccountDTO> getClientCurrentAccounts(Authentication authentication) {
        return accountService.getClientCurrentAccountsDTO(authentication.getName());
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

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication) {

        Client authClient = clientService.findByEmail(authentication.getName());

        if (authClient != null) {
            if(authClient.getAccounts().size() >= 3){
                return new ResponseEntity<>("You can't create more than 3 accounts.", HttpStatus.FORBIDDEN);
            }

            String formattedAccountNumber = getRandomAccountNumber(accountService);

            Account newAccount = new Account(formattedAccountNumber, LocalDate.now(), 0.0, true);
            authClient.addAccount(newAccount);
            accountService.saveAccount(newAccount);

            return new ResponseEntity<>("Account has been created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Unknown user", HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping("/clients/current/accounts/{id}")
    public ResponseEntity<Object> removeAccount(@PathVariable Long id, Authentication authentication) {

        Client authClient = clientService.findByEmail(authentication.getName());

        if(id == null) {
            return new ResponseEntity<>("Unknown account", HttpStatus.FORBIDDEN);
        }

        Account account = accountService.findById(id);
        if(account == null) {
            return new ResponseEntity<>("This account doesn't exist in the database", HttpStatus.FORBIDDEN);
        }

        if(authClient.getAccounts().size() <= 1) {
            return new ResponseEntity<>("You can't remove your only account", HttpStatus.FORBIDDEN);
        }
        if(account.getActive().toString().isBlank()) {
            return new ResponseEntity<>("This account doesn't have a state", HttpStatus.FORBIDDEN);
        }
        if(!account.getActive()){
            return new ResponseEntity<>("This account is already removed", HttpStatus.FORBIDDEN);
        }
        if(!accountService.existsByIdAndClient_Id(id, authClient.getId())) {
            return new ResponseEntity<>("This account doesn't belong to the current user", HttpStatus.FORBIDDEN);
        }
        if(account.getBalance() > 0) {
            return new ResponseEntity<>("You can't remove an account with money", HttpStatus.FORBIDDEN);
        }

        Set<Transaction> transactions = transactionService.findByAccount_Id(id);
        if(transactions != null) {
            transactions.forEach(transaction -> {
                transaction.setActive(false);
                transactionService.saveTransaction(transaction);
            });
        }

        account.setActive(false);
        accountService.saveAccount(account);

        return new ResponseEntity<>("Account removed successfully", HttpStatus.OK);
    }
}
