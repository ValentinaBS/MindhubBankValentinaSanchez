package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.*;

public interface CardService {
    boolean existsByNumber(String number);
    boolean existsByClientAndColorAndTypeAndIsActive(Client client, CardColor color, CardType type, Boolean isActive);
    boolean existsByIdAndClient_Id(Long id, Long clientId);

    Card findById(Long id);
    void saveCard(Card card);
    void deleteCard(Card card);
}
