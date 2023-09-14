package com.mindhub.homebanking.utils;

import java.util.regex.Pattern;

public class LoanUtils {

    private LoanUtils() {}

    public static boolean regExpAmountValidation(Double input) {
        return Pattern.matches("[0-9]+\\.[0-9]{1,2}", input.toString());
    }
}
