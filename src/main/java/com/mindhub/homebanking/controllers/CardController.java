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

import static com.mindhub.homebanking.utils.CardUtils.getCVV;
import static com.mindhub.homebanking.utils.CardUtils.getCardNumber;

@RestController
// Methods in a RestController return JSON objects or XML. This controller will work with API REST.
// They listen and respond petitions
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;

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

            boolean filteredCardsByColorAndType = cardService.existsByClientAndColorAndTypeAndIsActive(authClient, color, type, true);
            if(filteredCardsByColorAndType) {
                return new ResponseEntity<>("You can't create another " + color.toString().toLowerCase() + " card in " + type.toString().toLowerCase(), HttpStatus.FORBIDDEN);
            }

            String cardNumber = getCardNumber(cardService);
            int cvv = getCVV();

            Card newCard = new Card(cardHolder, type, color, cardNumber, cvv, LocalDate.now(), LocalDate.now().plusYears(5), true);
            authClient.addCard(newCard);
            cardService.saveCard(newCard);

            return new ResponseEntity<>("Card has been created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Unknown user", HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping("/clients/current/cards/{id}")
    public ResponseEntity<Object> removeCard(@PathVariable Long id, Authentication authentication) {

        Client authClient = clientService.findByEmail(authentication.getName());

        if(id == null) {
            return new ResponseEntity<>("Unknown card", HttpStatus.FORBIDDEN);
        }

        Card card = cardService.findById(id);
        if(card == null) {
            return new ResponseEntity<>("This card doesn't exist in the database", HttpStatus.FORBIDDEN);
        }
        if(card.getIsActive().toString().isBlank()) {
            return new ResponseEntity<>("This card doesn't have a state", HttpStatus.FORBIDDEN);
        }
        if(!card.getIsActive()){
            return new ResponseEntity<>("This card is already removed", HttpStatus.FORBIDDEN);
        }
        if(!cardService.existsByIdAndClient_Id(id, authClient.getId())) {
            return new ResponseEntity<>("This card doesn't belong to the current user", HttpStatus.FORBIDDEN);
        }

        card.setIsActive(false);
        cardService.saveCard(card);

        return new ResponseEntity<>("Card removed successfully", HttpStatus.OK);
    }
}
