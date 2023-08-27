package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.WebAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
// Configures the Spring Security module before running the app
public class WebAuthorization {

    // We want to add something to our app context and run it first
    @Bean
    // HttpSecurity allows configuring web based security for specific http requests.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // We assign who has access to which endpoint
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/clients", "/api/login", "/api/logout").permitAll()
                .antMatchers("/web/index.html", "/web/styles.css", "/web/js/**", "/web/images/**", "/api/clients/current").permitAll()

                .antMatchers("/rest/**", "/h2-console/**", "/web/adminPages/**").hasAuthority("ADMIN")

                .antMatchers("/web/pages/**", "/api/clients/current/accounts", "/api/clients/current/cards", "/api/accounts/{id}").hasAuthority("CLIENT")

                .anyRequest().denyAll();

        // Endpoint for login and username and password parameters
        http.formLogin()

                .usernameParameter("email")

                .passwordParameter("password")

                .loginPage("/api/login");

        // Servlet
        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF(Cross-Site Request Forgery) tokens
        // Two step token validation to avoid copying it
        http.csrf().disable();

        // disabling frameOptions so h2-console can be accessed
        http.headers().frameOptions().disable();

        // if user is not authenticated, just send an authentication failure response in case an unidentified/unauthorized user tries to access a page.
        // Exception handler. req, what we send to the server. res, what we will return to the user. exc, error.
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        // We avoid asking the user to authenticate themselves every time they change pages
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        return http.build();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        // Cleans exceptions whenever authentication fails
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

    }
}
