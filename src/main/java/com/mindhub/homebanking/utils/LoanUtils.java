package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.models.Loan;

import java.util.regex.Pattern;

public class LoanUtils {

    private LoanUtils() {}

    public static boolean regExpAmountValidation(Double input) {
        return Pattern.matches("[0-9]+\\.[0-9]{1,2}", input.toString());
    }

    public static double getFinalAmount(LoanApplicationDTO loanApplicationDTO, Loan loan) {
        return loanApplicationDTO.getAmount() + (loanApplicationDTO.getAmount() * (loan.getPercentage() / 100.0));
    }
}
