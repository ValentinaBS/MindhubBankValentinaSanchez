package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.configurations.WebAuthentication;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientRole;
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
// They listen and respond petitions
@RequestMapping("/api")
public class ClientController {

    @Autowired
    // Injects ClientRepository to use it in this controller
    private ClientRepository clientRepository;
    // Interface. We do this to use methods with clientRepository.
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
    } // Servlet (listens and responds to specific HTTP petitions and business logic). Tomcat is a servlet container.

    @RequestMapping("/clients/current")
    public ClientDTO getClientCurrent (Authentication authentication) {
        return new ClientDTO(clientRepository.findByEmail(authentication.getName()));
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (clientRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }
        if (password.length() < 8) {
            return new ResponseEntity<>("The password is too short", HttpStatus.BAD_REQUEST);
        }
        if(password.length() > 15){
            return new ResponseEntity<>("The password is too long", HttpStatus.BAD_REQUEST);
        }

        if (firstName.equalsIgnoreCase("admin") && email.toLowerCase().startsWith("admin")) {
            clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
            return new ResponseEntity<>("Admin has been created successfully", HttpStatus.CREATED);
        } else {
            clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
            return new ResponseEntity<>("Client has been created successfully", HttpStatus.CREATED);
        }

    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return new ClientDTO(clientRepository.findById(id).orElse(null));
        // Since it's only one Client, stream and map are not needed
    } // Servlet. Tomcat is a servlet container.
}
