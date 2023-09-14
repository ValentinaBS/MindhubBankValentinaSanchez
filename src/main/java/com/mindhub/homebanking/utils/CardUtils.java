package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.services.CardService;

// final so it can´t inherit/extends
public final class CardUtils {

    // You can't create an instance for this class since the constructor is private
    private CardUtils(){}

    public static String getCardNumber(CardService cardService) {
        String cardNumber;

        do {
            // Create a random card number with the 0000-0000-0000-0000 structure
            cardNumber = (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000)
                    + "-" + (int)((Math.random() * (9999-1000)) + 1000);
        } while (cardService.existsByNumber(cardNumber));

        return cardNumber;
    }

    public static int getCVV(){
        // Create a random cvv from 100 to 999
        return (int) ((Math.random() * (999 - 100)) + 100);
    }
}

