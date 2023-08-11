package com.mindhub.homebanking.repositories;
import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
// ClientRepository inherits from JpaRepository
public interface ClientRepository extends JpaRepository<Client, Long> {
} // Creates a repository to save the clients in the database