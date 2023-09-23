package com.mindhub.homebanking;
/*
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoriesTest {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void existsLoans(){

        List<Loan> loans = loanRepository.findAll();

        assertThat(loans,is(not(empty())));

    }
    @Test
    public void existsPersonalLoan(){

        List<Loan> loans = loanRepository.findAll();

        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));

    }

    @Test
    public void existsAccounts(){

        List<Account> accounts = accountRepository.findAll();

        assertThat(accounts, is(not(empty())));

    }
    @Test
    public void existsAccountNumber(){

        List<Account> accounts = accountRepository.findAll();

        assertThat(accounts, hasItem(hasProperty("number", is(notNullValue()))));

    }

    @Test
    public void existsClients(){

        List<Client> clients = clientRepository.findAll();

        assertThat(clients, is(not(empty())));

    }
    @Test
    public void existsClientFirstName(){

        List<Client> clients = clientRepository.findAll();

        assertThat(clients, hasItem(hasProperty("firstName", is(notNullValue()))));

    }

    @Test
    public void existsCards(){

        List<Card> cards = cardRepository.findAll();

        assertThat(cards, is(not(empty())));

    }
    @Test
    public void existsCardColor(){

        List<Card> cards = cardRepository.findAll();

        assertThat(cards, hasItem(hasProperty("color", is(notNullValue()))));

    }

    @Test
    public void existsTransactions(){

        List<Transaction> transactions = transactionRepository.findAll();

        assertThat(transactions, is(not(empty())));

    }
    @Test
    public void existsTransactionAmount(){

        List<Transaction> transactions = transactionRepository.findAll();

        assertThat(transactions, hasItem(hasProperty("amount", is(notNullValue()))));

    }
}
*/