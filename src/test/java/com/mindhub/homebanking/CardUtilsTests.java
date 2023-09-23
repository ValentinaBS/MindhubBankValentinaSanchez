package com.mindhub.homebanking;
/*
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CardUtilsTests {

    @Autowired
    private CardService cardService;

    private CardUtilsTests() {}
    @Test
    public void cardNumberIsCreated(){

        String cardNumber = CardUtils.getCardNumber(cardService);

        assertThat(cardNumber, is(not(emptyOrNullString())));

    }
    @Test
    public void cardNumberHas19Chars(){

        String cardNumber = CardUtils.getCardNumber(cardService);

        assertEquals(19, cardNumber.length());

    }

    @Test
    public void cvvIsCreated(){

        int cvv = CardUtils.getCVV();

        assertThat(cvv, is(notNullValue()));

    }
    @Test
    public void cvvHas3Digits(){

        int cvv = CardUtils.getCVV();
        String cvvString = String.valueOf(cvv);

        assertEquals(3, cvvString.length());

    }
}
*/