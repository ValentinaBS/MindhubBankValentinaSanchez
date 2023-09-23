package com.mindhub.homebanking.controllers;
import java.time.LocalDate;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mindhub.homebanking.utils.ClientUtils.regExpEmailValidation;
import static com.mindhub.homebanking.utils.ClientUtils.regExpPassValidation;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
// We put it in Spring's context.
// Defines this class as a Rest Controller. They listen and respond petitions.
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientService.getClientsDTO();
    } // Servlet (Microprogram. listens and responds to specific HTTP petitions and business logic). Tomcat is a servlet container.

    @GetMapping("/clients/current")
    // an instance of the Authentication class contains info about the current user
    public ClientDTO getClientCurrent(Authentication authentication) {
        return clientService.getCurrentClient(authentication.getName());
    }

    @PostMapping("/clients")
    // ResponseEntity contains HTTP status code and an optional JSON body with more info
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            return new ResponseEntity<>("Please don't leave any empty fields.", HttpStatus.FORBIDDEN);
        }
        if (clientService.existsByEmail(email)) {
            return new ResponseEntity<>("Email already in use.", HttpStatus.FORBIDDEN);
        }
        if(!regExpEmailValidation(email)){
            return new ResponseEntity<>("Your email address must be in the next format: email@example.com", HttpStatus.FORBIDDEN);
        }
        if(!regExpPassValidation(password)){
            return new ResponseEntity<>("Your password must have between 8-15 characters, one uppercase letter, one lowercase letter, one number and one special character.", HttpStatus.FORBIDDEN);
        }

        if (firstName.equalsIgnoreCase("admin") && email.toLowerCase().startsWith("admin")) {

            clientService.saveClient(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
            return new ResponseEntity<>("Admin has been created successfully", HttpStatus.CREATED);

        } else {

            Client newClient = new Client(firstName, lastName, email, passwordEncoder.encode(password));
            clientService.saveClient(newClient);

            String formattedAccountNumber = AccountUtils.getRandomAccountNumber(accountService);

            // Creates default account to associate it to a newly registered client
            Account defaultAccount = new Account(formattedAccountNumber, LocalDate.now(), 0.0, true, AccountType.SAVINGS);
            newClient.addAccount(defaultAccount);
            accountService.saveAccount(defaultAccount);

            return new ResponseEntity<>("Client has been created successfully", HttpStatus.CREATED);
        }
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientService.getClientDTO(id);
    }
}
