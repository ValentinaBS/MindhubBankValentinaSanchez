package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.mindhub.homebanking.utils.TransactionUtils.regExpAmountValidation;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam Double amount, @RequestParam String description,
                                                    @RequestParam String accountOrigin, @RequestParam String accountDestination, Authentication authentication) {

        Client authClient = clientService.findByEmail(authentication.getName());

        if (authClient != null) {
            if (amount == null || amount <= 0.0) {
                return new ResponseEntity<>("You must specify an amount", HttpStatus.FORBIDDEN);
            }
            if (accountOrigin.isBlank()) {
                return new ResponseEntity<>("You must specify the origin account", HttpStatus.FORBIDDEN);
            }
            if (accountDestination.isBlank()) {
                return new ResponseEntity<>("You must specify the destination account", HttpStatus.FORBIDDEN);
            }
            if (description.isBlank()) {
                return new ResponseEntity<>("You must write a description", HttpStatus.FORBIDDEN);
            }
            if (!regExpAmountValidation(amount)) {
                return new ResponseEntity<>("Enter an amount with the next format: 1000.00", HttpStatus.FORBIDDEN);
            }
            if (accountOrigin.equals(accountDestination)){
                return new ResponseEntity<>("You can't transfer money to the same account", HttpStatus.FORBIDDEN);
            }

            Account originAccount = accountService.findByNumber(accountOrigin);
            Account destinationAccount = accountService.findByNumber(accountDestination);

            if (!authClient.getAccounts().contains(originAccount)) {
                return new ResponseEntity<>("This account doesn't belong to the current user", HttpStatus.FORBIDDEN);
            }
            if (amount > originAccount.getBalance()) {
                return new ResponseEntity<>("You don't have enough funds to transfer", HttpStatus.FORBIDDEN);
            }

            Transaction debitTransaction = new Transaction(-amount, description + " | " + accountOrigin, LocalDateTime.now(), TransactionType.DEBIT);
            originAccount.addTransaction(debitTransaction);
            transactionService.saveTransaction(debitTransaction);

            Transaction creditTransaction = new Transaction(amount, description + " | " + accountDestination, LocalDateTime.now(), TransactionType.CREDIT);
            destinationAccount.addTransaction(creditTransaction);
            transactionService.saveTransaction(creditTransaction);

            originAccount.setBalance(originAccount.getBalance() - amount);
            accountService.saveAccount(originAccount);

            destinationAccount.setBalance(destinationAccount.getBalance() + amount);
            accountService.saveAccount(destinationAccount);

            return new ResponseEntity<>("The transaction has been created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Unknown user", HttpStatus.UNAUTHORIZED);
    }
}
