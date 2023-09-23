package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.services.AccountService;

import java.util.Random;

public final class AccountUtils {

    private AccountUtils() {}

    public static String getRandomAccountNumber(AccountService accountService) {
        String formattedAccountNumber;
        int randomNumber;
        do {
            // Generates a random number between 0 (inclusive) and 99999999 (exclusive)
            randomNumber = new Random().nextInt(100000000);
            // Ensures that the output will always be in an 8-digit format
            formattedAccountNumber = "VIN-" + String.format("%08d", randomNumber);
        } while (accountService.existsByNumber(formattedAccountNumber)); // Avoids repeated account numbers
        return formattedAccountNumber;
    }
}
