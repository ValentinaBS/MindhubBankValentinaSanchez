package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ClientLoanService clientLoanService;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanService.getLoansDTO();
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication) {

        Client authClient = clientService.findByEmail(authentication.getName());

            if (loanApplicationDTO.getDestinataryAccountNumber().isBlank()) {
                return new ResponseEntity<>("You must specify an account number", HttpStatus.FORBIDDEN);
            }
            if (loanApplicationDTO.getAmount() <= 0.0 || loanApplicationDTO.getAmount() == null) {
                return new ResponseEntity<>("You must specify an amount", HttpStatus.FORBIDDEN);
            }
            if (loanApplicationDTO.getPayments() <= 0  || loanApplicationDTO.getPayments() == null) {
                return new ResponseEntity<>("You must specify the installments", HttpStatus.FORBIDDEN);
            }

            Loan loan = loanService.findById(loanApplicationDTO.getId());
            if (loan == null) {
                return new ResponseEntity<>("This kind of loan doesn't exist", HttpStatus.FORBIDDEN);
            }
            Account account = accountService.findByNumber(loanApplicationDTO.getDestinataryAccountNumber());
            if (account == null) {
                return new ResponseEntity<>("The destination account doesn't exist", HttpStatus.FORBIDDEN);
            }

            if(!(accountService.existsByIdAndClient_Id(account.getId(), authClient.getId()))) {
                return new ResponseEntity<>("The destination account doesn't belong to the current user", HttpStatus.FORBIDDEN);
            }
            if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
                return new ResponseEntity<>("The requested amount is higher than the loan's max amount", HttpStatus.FORBIDDEN);
            }
            if (!(loan.getPayments().contains(loanApplicationDTO.getPayments()))) {
                return new ResponseEntity<>("This loan doesn't have " + loanApplicationDTO.getPayments().toString() + " installments", HttpStatus.FORBIDDEN);
            }
            if(clientLoanService.existsByClientAndLoan(authClient, loan)){
                return new ResponseEntity<>("This kind of loan has been requested already", HttpStatus.FORBIDDEN);
            }

            ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount() * 1.20 , loanApplicationDTO.getPayments());
            clientLoan.setClient(authClient);
            clientLoan.setLoan(loan);
            clientLoanService.saveClientLoan(clientLoan);

            Transaction creditTransaction = new Transaction(loanApplicationDTO.getAmount(), loan.getName() + " | Loan Approved", LocalDateTime.now(), TransactionType.CREDIT);
            creditTransaction.setAccount(account);
            transactionService.saveTransaction(creditTransaction);

            account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());
            accountService.saveAccount(account);

            return new ResponseEntity<>("The loan has been requested successfully", HttpStatus.CREATED);

    }
}
