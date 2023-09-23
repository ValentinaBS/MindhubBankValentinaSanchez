package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {
    //Abstract methods. They don't have the implementation with all the logic.

    Client findByEmail(String email);
    Client findById(Long id);
    boolean existsByEmail(String email);

    ClientDTO getClientDTO(Long id);
    List<ClientDTO> getClientsDTO();
    ClientDTO getCurrentClient(String email);
    void saveClient(Client client);

}
