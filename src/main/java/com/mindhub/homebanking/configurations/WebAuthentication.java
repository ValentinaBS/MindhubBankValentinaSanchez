package com.mindhub.homebanking.configurations;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

// Configures the Spring Security module before running the app
@Configuration
// Inherits methods from the GlobalAuthenticationConfigurerAdapter class
public class WebAuthentication extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    private ClientRepository clientRepository;

    @Bean
    // We can use PasswordEncoder in any part of the app to encrypt a password
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    // Override method init from GlobalAuthenticationConfigurerAdapter
    public void init(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(inputEmail-> {

            Client client = clientRepository.findByEmail(inputEmail);

            if (client != null) {
                // Creates a new cookie for the user, a new session in the server.
                // The browser can make petitions with that cookie and compare it to the session in the server.
                if (client.getFirstName().equalsIgnoreCase("admin") && client.getEmail().toLowerCase().startsWith("admin")) {
                    return new User(client.getEmail(), client.getPassword(),
                            AuthorityUtils.createAuthorityList("ADMIN"));
                }
                // User represents an authenticated user, logged in. A registered user is client.
                return new User(client.getEmail(), client.getPassword(),
                        AuthorityUtils.createAuthorityList("CLIENT"));
            }
            else {
                // Throws an exception in case a username doesn't match
                throw new UsernameNotFoundException("Unknown user email: " + inputEmail);
            }

        });

    }

}
