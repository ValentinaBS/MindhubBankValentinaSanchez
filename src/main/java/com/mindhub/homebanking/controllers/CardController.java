package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
// They listen and respond petitions
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;

    private String getRandomCardNumber() {
        String cardNumber;
        do {
            // Create a random card number with the 0000-0000-0000-0000 structure
            cardNumber = (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000);
        } while (cardRepository.existsByNumber(cardNumber)); // Avoid repeated card numbers
        return cardNumber;
    }

    private int getRandomCvvNumber(){
        // Create a random cvv from 100 to 999
        return (int) ((Math.random() * (999 - 100)) + 100);
    }

    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createCards(@RequestParam CardColor color, @RequestParam CardType type, Authentication authentication) {

        Client authClient = clientRepository.findByEmail(authentication.getName());
        String cardHolder = authClient.getFirstName() + " " + authClient.getLastName();
        List<Card> filteredCardsByType = cardRepository.findByClientAndType(authClient, type);
        List<Card> filteredCardsByColorAndType = cardRepository.findByClientAndColorAndType(authClient, color, type);

        if (color == null || type == null) {
            return new ResponseEntity<>("Please don't leave any empty fields.", HttpStatus.FORBIDDEN);
        }
        if(filteredCardsByType.size() >= 3){
            return new ResponseEntity<>("You can't create more than 3 " + type.toString().toLowerCase() + " cards.", HttpStatus.FORBIDDEN);
        }
        if(!filteredCardsByColorAndType.isEmpty()) {
            return new ResponseEntity<>("You can't create another " + color.toString().toLowerCase() + " card in " + type.toString().toLowerCase(), HttpStatus.FORBIDDEN);
        }

        String cardNumber = getRandomCardNumber();
        int cvv = getRandomCvvNumber();

        Card newCard = new Card(cardHolder, type, color, cardNumber, cvv, LocalDate.now(), LocalDate.now().plusYears(5));
        authClient.addCard(newCard);
        cardRepository.save(newCard);

        return new ResponseEntity<>("Card has been created successfully", HttpStatus.CREATED);
    }
}
