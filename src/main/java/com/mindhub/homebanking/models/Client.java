package com.mindhub.homebanking.models;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity // Creates an empty table
public class Client {

    // ---- Properties ----
    @Id // Indicates Primary Key
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native") // Generates ID value automatically
    private long id;
    private String firstName;
    private String lastName;
    private String email;

    // ---- Relations ----
    @OneToMany(mappedBy="client", fetch= FetchType.EAGER)
    // How do we want to bring the data. EAGER brings Client with Account. LAZY brings Client only.
    private Set<Account> accounts = new HashSet<>();
    // Initialising a space to save all accounts, without duplicates

    // ---- Constructors ----
    public Client(){ } // It's used to map by Hibernate. DTOs don't because they don´t persist.
    public Client(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // ---- Getters & Setters ----
    public long getId() {return id;}
    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }
    public void addAccount(Account account) {
        account.setClient(this);
        accounts.add(account);
    }

}
