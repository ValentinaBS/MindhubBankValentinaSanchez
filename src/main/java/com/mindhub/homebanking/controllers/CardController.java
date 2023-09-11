package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
// They listen and respond petitions
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;

    private String getRandomCardNumber() {
        String cardNumber;
        do {
            // Create a random card number with the 0000-0000-0000-0000 structure
            cardNumber = (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000);
        } while (cardService.existsByNumber(cardNumber)); // Avoid repeated card numbers
        return cardNumber;
    }

    private int getRandomCvvNumber(){
        // Create a random cvv from 100 to 999
        return (int) ((Math.random() * (999 - 100)) + 100);
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam CardColor color, @RequestParam CardType type, Authentication authentication) {

        Client authClient = clientService.findByEmail(authentication.getName());

        if (authClient != null) {
            String cardHolder = authClient.getFirstName() + " " + authClient.getLastName();

            if(type.toString().isBlank()) {
                return new ResponseEntity<>("You must select a type of card", HttpStatus.FORBIDDEN);
            }
            if(type.toString().isBlank()) {
                return new ResponseEntity<>("You must select a card color", HttpStatus.FORBIDDEN);
            }

            boolean filteredCardsByColorAndType = cardService.existsByClientAndColorAndType(authClient, color, type);
            if(filteredCardsByColorAndType) {
                return new ResponseEntity<>("You can't create another " + color.toString().toLowerCase() + " card in " + type.toString().toLowerCase(), HttpStatus.FORBIDDEN);
            }

            String cardNumber = getRandomCardNumber();
            int cvv = getRandomCvvNumber();

            Card newCard = new Card(cardHolder, type, color, cardNumber, cvv, LocalDate.now(), LocalDate.now().plusYears(5));
            authClient.addCard(newCard);
            cardService.saveCard(newCard);

            return new ResponseEntity<>("Card has been created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Unknown user", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/clients/current/cards/{id}")
    public ResponseEntity<Object> deleteCard(@PathVariable Long id) {
        Card card = cardService.findById(id);
        cardService.deleteCard(card);
        return new ResponseEntity<>("Card deleted successfully", HttpStatus.OK);
    }
}
