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
    ClientRepository clientRepository;

    @Bean
    // We can use PasswordEncoder in any part of the app
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    // Override method init from GlobalAuthenticationConfigurerAdapter
    public void init(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(inputName-> {

            Client client = clientRepository.findByEmail(inputName);

            if (client != null) {
                // Creates a new cookie for the user, a new session.
                // We build the authenticated User with username, password and authority
                if (client.getFirstName().equalsIgnoreCase("admin") && client.getEmail().toLowerCase().startsWith("admin")) {
                    return new User(client.getEmail(), client.getPassword(),
                            AuthorityUtils.createAuthorityList("ADMIN"));
                }
                return new User(client.getEmail(), client.getPassword(),
                        AuthorityUtils.createAuthorityList("CLIENT"));

            }
            else {
                // Throws an exception in case a username doesn't match
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }

        });

    }

}
