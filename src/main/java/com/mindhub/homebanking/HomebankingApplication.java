package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean // Run this first, can only be used in methods
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository) {
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

			// Assign accounts to clients
			account1.setClient(client1);
			account2.setClient(client1);
			account3.setClient(client2);
			account4.setClient(client2);

			// Save accounts to the database
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);
		};
	}
}
