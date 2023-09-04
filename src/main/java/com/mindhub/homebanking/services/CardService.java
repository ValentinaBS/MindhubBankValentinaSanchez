package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.*;

public interface CardService {
    boolean existsByNumber(String number);
    boolean existsByClientAndColorAndType(Client client, CardColor color, CardType type);

    void saveCard(Card card);
}
