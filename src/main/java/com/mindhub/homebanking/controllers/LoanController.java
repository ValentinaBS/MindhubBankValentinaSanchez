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

import static com.mindhub.homebanking.utils.LoanUtils.getFinalAmount;
import static com.mindhub.homebanking.utils.LoanUtils.regExpAmountValidation;


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
    public ResponseEntity<Object> createClientLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication) {

            if (loanApplicationDTO.getDestinataryAccountNumber().isBlank()) {
                return new ResponseEntity<>("You must specify an account number.", HttpStatus.FORBIDDEN);
            }
            if (loanApplicationDTO.getAmount() <= 0.0 || loanApplicationDTO.getAmount() == null) {
                return new ResponseEntity<>("You must specify an amount", HttpStatus.FORBIDDEN);
            }
            if (loanApplicationDTO.getPayments() <= 0  || loanApplicationDTO.getPayments() == null) {
                return new ResponseEntity<>("You must specify the installments.", HttpStatus.FORBIDDEN);
            }
            if (!regExpAmountValidation(loanApplicationDTO.getAmount())) {
                return new ResponseEntity<>("Please enter a numeric amount with the next format: 1000.00", HttpStatus.FORBIDDEN);
            }

            Loan loan = loanService.findById(loanApplicationDTO.getId());
            if (loan == null) {
                return new ResponseEntity<>("This kind of loan doesn't exist.", HttpStatus.FORBIDDEN);
            }
            Account account = accountService.findByNumber(loanApplicationDTO.getDestinataryAccountNumber());
            if (account == null) {
                return new ResponseEntity<>("The destination account doesn't exist.", HttpStatus.FORBIDDEN);
            }
            if (!account.getActive()) {
                return new ResponseEntity<>("The destination account isn't active.", HttpStatus.FORBIDDEN);
            }

            Client authClient = clientService.findByEmail(authentication.getName());
            if(!(accountService.existsByIdAndClient_Id(account.getId(), authClient.getId()))) {
                return new ResponseEntity<>("The destination account doesn't belong to the current user.", HttpStatus.FORBIDDEN);
            }
            if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
                return new ResponseEntity<>("The requested amount is higher than the loan's max amount.", HttpStatus.FORBIDDEN);
            }
            if (!(loan.getPayments().contains(loanApplicationDTO.getPayments()))) {
                return new ResponseEntity<>("This loan doesn't have " + loanApplicationDTO.getPayments().toString() + " installments", HttpStatus.FORBIDDEN);
            }
            if(clientLoanService.existsByClientAndLoan(authClient, loan)){
                return new ResponseEntity<>("This kind of loan has been requested already.", HttpStatus.FORBIDDEN);
            }

            double finalAmount = getFinalAmount(loanApplicationDTO, loan);

            ClientLoan clientLoan = new ClientLoan(finalAmount, loanApplicationDTO.getPayments(), loanApplicationDTO.getPayments(), true);
            clientLoan.setClient(authClient);
            clientLoan.setLoan(loan);
            clientLoanService.saveClientLoan(clientLoan);

            Transaction creditTransaction = new Transaction(loanApplicationDTO.getAmount(), loan.getName() + " | Loan Approved", LocalDateTime.now(), TransactionType.CREDIT, account.getBalance() + loanApplicationDTO.getAmount(), true);
            creditTransaction.setAccount(account);
            transactionService.saveTransaction(creditTransaction);

            account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());
            accountService.saveAccount(account);

            return new ResponseEntity<>("The loan has been requested successfully.", HttpStatus.CREATED);

    }

    @Transactional
    @PatchMapping("/loans/{id}")
    public ResponseEntity<Object> payLoan(@PathVariable Long id, @RequestParam String accountNumber, Authentication authentication) {

        if (accountNumber.isBlank()) {
            return new ResponseEntity<>("You must specify an account number.", HttpStatus.FORBIDDEN);
        }

        ClientLoan clientLoan = clientLoanService.findById(id);
        if (clientLoan == null) {
            return new ResponseEntity<>("This loan doesn't exist.", HttpStatus.FORBIDDEN);
        }

        Account account = accountService.findByNumber(accountNumber);
        if (account == null) {
            return new ResponseEntity<>("The account doesn't exist.", HttpStatus.FORBIDDEN);
        }
        if (account.getBalance() <= 0) {
            return new ResponseEntity<>("The account doesn't have enough funds.", HttpStatus.FORBIDDEN);
        }
        if (!account.getActive()) {
            return new ResponseEntity<>("The account isn't active.", HttpStatus.FORBIDDEN);
        }

        Client authClient = clientService.findByEmail(authentication.getName());
        if(!(accountService.existsByIdAndClient_Id(account.getId(), authClient.getId()))) {
            return new ResponseEntity<>("The account doesn't belong to the current user.", HttpStatus.FORBIDDEN);
        }

        double amountToPay = clientLoan.getAmount() / clientLoan.getPaymentsLeft();
        if (amountToPay <= 0 || clientLoan.getPaymentsLeft() <= 0) {
            return new ResponseEntity<>("There are no more installments to pay.", HttpStatus.FORBIDDEN);
        }
        if (amountToPay > account.getBalance()) {
            return new ResponseEntity<>("You don't have enough funds to pay this installment.", HttpStatus.FORBIDDEN);
        }

        clientLoan.setAmount(clientLoan.getAmount() - amountToPay);
        clientLoan.setPaymentsLeft(clientLoan.getPaymentsLeft() - 1);
        if (clientLoan.getPaymentsLeft() == 0) {
            clientLoan.setActive(false);
        }
        clientLoanService.saveClientLoan(clientLoan);

        Transaction debitTransaction = new Transaction(-amountToPay, clientLoan.getLoan().getName() + " | Installment payment", LocalDateTime.now(), TransactionType.DEBIT, account.getBalance() - amountToPay, true);
        account.addTransaction(debitTransaction);
        transactionService.saveTransaction(debitTransaction);

        account.setBalance(account.getBalance() - amountToPay);
        accountService.saveAccount(account);

        return new ResponseEntity<>("The installment has been paid successfully.", HttpStatus.CREATED);
    }

    @PostMapping("/loans/create")
    public ResponseEntity<Object> createLoan(@RequestBody LoanDTO loanDTO) {

        if (loanDTO.getName().isBlank() || loanDTO.getMaxAmount().toString().isBlank() || loanDTO.getPayments().isEmpty() || loanDTO.getPercentage().toString().isBlank()) {
            return new ResponseEntity<>("Please don't leave any empty fields.", HttpStatus.FORBIDDEN);
        }
        if (loanService.existsByName(loanDTO.getName())) {
            return new ResponseEntity<>("There is an existing loan with that name already.", HttpStatus.FORBIDDEN);
        }
        if (!regExpAmountValidation(loanDTO.getMaxAmount())) {
            return new ResponseEntity<>("Please enter a numeric amount with the next format: 1000.00", HttpStatus.FORBIDDEN);
        }
        if (!regExpAmountValidation(loanDTO.getPercentage())) {
            return new ResponseEntity<>("Please enter a numeric amount with the next format: 10.25", HttpStatus.FORBIDDEN);
        }
        if (loanDTO.getMaxAmount() <= 0 || loanDTO.getPercentage() <= 0) {
            return new ResponseEntity<>("Please enter a number higher than 0.", HttpStatus.FORBIDDEN);
        }

        Loan loan = new Loan(loanDTO.getName(), loanDTO.getMaxAmount(), loanDTO.getPayments(), loanDTO.getPercentage());
        loanService.saveLoan(loan);

        return new ResponseEntity<>("A loan has been created successfully.", HttpStatus.CREATED);
    }
}
