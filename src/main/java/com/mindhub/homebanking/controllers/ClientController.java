package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController // Methods in a RestController return JSON objects or XML. Spring doesn't know it exists if it doesn't have this annotation.
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository; // Interface. We do this to use methods, not properties.

    @RequestMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientRepository
                .findAll()
                .stream()
                .map(ClientDTO::new)
                .collect(toList());
    } // Servlet

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientRepository
                .findById(id)
                .map(ClientDTO::new)
                .orElse(null);
    }
}
