package com.mindhub.homebanking.repositories;
import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
// ClientRepository inherits methods from JpaRepository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByEmail(String email);
    boolean existsByEmail(String email);
} // Creates a repository to save/modify/obtain/delete the clients in the database
