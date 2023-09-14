package com.mindhub.homebanking.utils;

import java.util.regex.Pattern;

public class ClientUtils {

    private ClientUtils() {};

    public static boolean regExpEmailValidation(String input) {
        return Pattern.matches("[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", input);
    }

    public static boolean regExpPassValidation(String input) {
        return Pattern.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}", input);
    }
}
