package com.mindhub.homebanking.controllers;
import java.time.LocalDate;
import java.util.Random;
import java.util.regex.*;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
// Defines this class as a Rest Controller. They listen and respond petitions.
@RequestMapping("/api")
public class ClientController {

    @Autowired
    // Injects ClientRepository to use it in this controller
    private ClientRepository clientRepository;
    // Interface. We do this to use methods with clientRepository.
    @Autowired
    // Injects AccountRepository to use it in this controller
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientRepository
                .findAll()
                .stream()
                // creates a stream object to use the map() method
                .map(ClientDTO::new)
                // runs the ClientDTO constructor and passes the current Client as argument.
                // returns a stream with the results .map(client -> new ClientDTO(client)
                .collect(toList());
                // converts stream back to a list
    } // Servlet (Microprogram. listens and responds to specific HTTP petitions and business logic). Tomcat is a servlet container.

    @RequestMapping("/clients/current")
    // an instance of the Authentication class contains info about the current user
    public ClientDTO getClientCurrent(Authentication authentication) {
        return new ClientDTO(clientRepository.findByEmail(authentication.getName()));
    }

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

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    // ResponseEntity contains HTTP status code and an optional JSON body with more info
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            return new ResponseEntity<>("Please don't leave any empty fields.", HttpStatus.FORBIDDEN);
        }
        if (clientRepository.existsByEmail(email)) {
            return new ResponseEntity<>("Email already in use.", HttpStatus.FORBIDDEN);
        }
        // Hacer método externo
        if(!(Pattern.matches("[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", email))){
            return new ResponseEntity<>("Your email address must be in the next format: email@example.com", HttpStatus.FORBIDDEN);
        }
        if(!(Pattern.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}", password))){
            return new ResponseEntity<>("Your password must have between 8-15 characters, one uppercase letter, one lowercase letter, one number and one special character.", HttpStatus.FORBIDDEN);
        }

        if (firstName.equalsIgnoreCase("admin") && email.toLowerCase().startsWith("admin")) {

            clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
            return new ResponseEntity<>("Admin has been created successfully", HttpStatus.CREATED);

        } else {

            Client newClient = new Client(firstName, lastName, email, passwordEncoder.encode(password));
            clientRepository.save(newClient);

            String formattedAccountNumber = getRandomAccountNumber();

            // Creates default account to associate it to a newly registered client
            Account defaultAccount = new Account(formattedAccountNumber, LocalDate.now(), 0.0);
            newClient.addAccount(defaultAccount);
            accountRepository.save(defaultAccount);

            return new ResponseEntity<>("Client has been created successfully", HttpStatus.CREATED);
        }
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        // Since it's only one Client, stream and map are not needed
        return new ClientDTO(clientRepository.findById(id).orElse(null));
    } // Servlet. Tomcat is a servlet container.
}
