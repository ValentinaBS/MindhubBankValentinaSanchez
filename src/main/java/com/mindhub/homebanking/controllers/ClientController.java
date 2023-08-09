package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
@RequestMapping("/api")
public class ClientController {

    @Autowired
    // Injects ClientRepository to use it in this controller
    private ClientRepository clientRepository;
    // Interface. We do this to use methods, not properties.

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
    } // Servlet. Tomcat is a servlet container.

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientRepository
                .findById(id)
                // Since it's only one Client, stream is not needed
                .map(ClientDTO::new)
                .orElse(null);
    } // Servlet. Tomcat is a servlet container.
}
