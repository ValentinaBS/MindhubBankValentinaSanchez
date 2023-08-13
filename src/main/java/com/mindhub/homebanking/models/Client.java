package com.mindhub.homebanking.models;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity // Creates an empty table
// public: any code can use the class. - private: can only be used inside the class.
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

    @OneToMany(mappedBy="client", fetch= FetchType.EAGER)
    private Set<ClientLoan> clientLoans = new HashSet<>();

    // ---- Constructors ----
    // Special method that creates an instance of a class, called with the new operator.
    public Client(){ } // It's used to map by Hibernate. DTOs don't because they don´t persist.
    public Client(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // ---- Getters & Setters ----
    public long getId() {return id;}

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

    public Set<ClientLoan> getClientLoans() {
        return clientLoans;
    }

    public void addClientLoan(ClientLoan clientLoan) {
        clientLoan.setClient(this);
        clientLoans.add(clientLoan);
    }

    public List<Loan> getLoans() {
        return clientLoans
                .stream()
                .map(ClientLoan::getLoan)
                .collect(toList());
    }
}
