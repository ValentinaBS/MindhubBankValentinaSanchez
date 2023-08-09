package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean // Run this first, can only be used in methods
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
		// CommandLineRunner are methods that can be implemented to run at application startup
		return (args) -> {
			// Create clients
			Client client1 = new Client("Melba", "Morel", "melba@mindhub.com");
			Client client2 = new Client("Alex", "Fulsch", "alexfu@mindhub.com");

			// Save clients to the database
			clientRepository.save(client1);
			clientRepository.save(client2);

			// Create accounts
			Account account1 = new Account("VIN-0001", LocalDate.now(), 5000.0);
			Account account2 = new Account("VIN-0002", LocalDate.now().plusDays(1), 7500.0);
			Account account3 = new Account("VIN-0003", LocalDate.now(), 9000.0);
			Account account4 = new Account("VIN-0004", LocalDate.now().plusDays(2), 3200.5);

			// Create transactions
			Transaction trans1 = new Transaction(-2300.5, "Rent", LocalDateTime.now(), TransactionType.DEBIT);
			Transaction trans2 = new Transaction(3550.0, "Wage pay", LocalDateTime.now(), TransactionType.CREDIT);
			Transaction trans3 = new Transaction(-1232.5, "Groceries", LocalDateTime.now(), TransactionType.DEBIT);
			Transaction trans4 = new Transaction(250.0, "Birthday gift", LocalDateTime.now(), TransactionType.CREDIT);

			// Assign accounts to clients
			client1.addAccount(account1);
			client1.addAccount(account2);
			client2.addAccount(account3);
			client2.addAccount(account4);

			// Assign transactions to accounts
			account1.addTransaction(trans1);
			account1.addTransaction(trans2);
			account2.addTransaction(trans3);
			account2.addTransaction(trans4);

			// Save accounts to the database
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);

			// Save transactions to the database
			transactionRepository.save(trans1);
			transactionRepository.save(trans2);
			transactionRepository.save(trans3);
			transactionRepository.save(trans4);
		};
	}
}
