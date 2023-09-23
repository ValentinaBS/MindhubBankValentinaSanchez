package com.mindhub.homebanking.controllers;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.lowagie.text.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.TransactionUtils.regExpAmountValidation;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/transactions")
    public ResponseEntity<Object> getTransactionsByDate(@RequestParam String dateStart,
                                                        @RequestParam String dateEnd,
                                                        @RequestParam String accountNumber,
                                                        Authentication authentication) throws IOException {

        Client authClient = clientService.findByEmail(authentication.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (authClient == null) {
            return new ResponseEntity<>("You are not allowed to see this.", HttpStatus.FORBIDDEN);
        }
        if (dateStart.isBlank()) {
            return new ResponseEntity<>("You can't leave the starting date empty.", HttpStatus.FORBIDDEN);
        }
        if (dateEnd.isBlank()) {
            return new ResponseEntity<>("You can't leave the end date empty.", HttpStatus.FORBIDDEN);
        }

        Account account = accountService.findByNumber(accountNumber);
        if (account == null) {
            return new ResponseEntity<>("This account doesn't exist.", HttpStatus.NOT_FOUND);
        }
        if (!accountService.existsByIdAndClient_Id(account.getId(), authClient.getId())) {
            return new ResponseEntity<>("This account doesn't belong to the current user.", HttpStatus.FORBIDDEN);
        }
        if (dateStart.equals(dateEnd)) {
            return new ResponseEntity<>("You can't select the same date.", HttpStatus.FORBIDDEN);
        }

        LocalDateTime dateInitFormatted = LocalDateTime.parse(dateStart, formatter);
        LocalDateTime dateEndFormatted = LocalDateTime.parse(dateEnd, formatter);
        List<Transaction> transactions = transactionService.findByTransferDateBetweenAndActiveAndAccount_Number(dateInitFormatted, dateEndFormatted, true, accountNumber);
        if (dateEndFormatted.isBefore(dateInitFormatted)) {
            return new ResponseEntity<>("The end date is before the start date.", HttpStatus.FORBIDDEN);
        }
        if (transactions.isEmpty()) {
            return new ResponseEntity<>("There are no transactions in this time period.", HttpStatus.FORBIDDEN);
        }


        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        PdfPTable logo = new PdfPTable(1);
        logo.setWidthPercentage(100);

        Image img = Image.getInstance("C:\\Users\\User\\Desktop\\homebanking\\homebanking\\src\\main\\resources\\static\\web\\images\\logo-blue.png");
        img.scaleToFit(200, 56);
        img.setAlignment(Image.ALIGN_BASELINE);
        PdfPCell imageCell = new PdfPCell(img);
        imageCell.setBorder(PdfPCell.NO_BORDER);
        logo.addCell(imageCell);

        doc.add(logo);

        PdfPTable tableTitle = new PdfPTable(1);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingBottom(20);
        cell.addElement(new Paragraph("Your requested transactions:", new Font(Font.HELVETICA, 16)));
        tableTitle.addCell(cell);
        doc.add(tableTitle);

        PdfPTable table = new PdfPTable(5);
        table.addCell("Type");
        table.addCell("Description");
        table.addCell("Amount");
        table.addCell("Date");
        table.addCell("Balance");

        for (Transaction transaction : transactions) {
            table.addCell(transaction.getType().toString());
            table.addCell(transaction.getDescription());
            table.addCell(transaction.getAmount().toString());
            table.addCell(transaction.getTransferDate().format(formatter));
            table.addCell(transaction.getCurrentBalance().toString());
        }
        doc.add(table);

        doc.close();

        byte[] pdf = out.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Transactions_Mindhub_Bank.pdf");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam Double amount, @RequestParam String description,
                                                    @RequestParam String accountOrigin, @RequestParam String accountDestination, Authentication authentication) {

        Client authClient = clientService.findByEmail(authentication.getName());

        if (authClient != null) {
            if (amount == null || amount <= 0.0) {
                return new ResponseEntity<>("You must specify an amount", HttpStatus.FORBIDDEN);
            }
            if (accountOrigin.isBlank()) {
                return new ResponseEntity<>("You must specify the origin account", HttpStatus.FORBIDDEN);
            }
            if (accountDestination.isBlank()) {
                return new ResponseEntity<>("You must specify the destination account", HttpStatus.FORBIDDEN);
            }
            if (description.isBlank()) {
                return new ResponseEntity<>("You must write a description", HttpStatus.FORBIDDEN);
            }
            if (!regExpAmountValidation(amount)) {
                return new ResponseEntity<>("Enter an amount with the next format: 1000.00", HttpStatus.FORBIDDEN);
            }
            if (accountOrigin.equals(accountDestination)){
                return new ResponseEntity<>("You can't transfer money to the same account", HttpStatus.FORBIDDEN);
            }

            Account originAccount = accountService.findByNumber(accountOrigin);
            Account destinationAccount = accountService.findByNumber(accountDestination);

            if (!authClient.getAccounts().contains(originAccount)) {
                return new ResponseEntity<>("This account doesn't belong to the current user", HttpStatus.FORBIDDEN);
            }
            if (amount > originAccount.getBalance()) {
                return new ResponseEntity<>("You don't have enough funds to transfer", HttpStatus.FORBIDDEN);
            }

            BigDecimal amountTwoDecimals = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);

            Transaction debitTransaction = new Transaction(-amountTwoDecimals.doubleValue(), description + " | " + accountOrigin, LocalDateTime.now(), TransactionType.DEBIT, originAccount.getBalance() - amount, true);
            originAccount.addTransaction(debitTransaction);
            transactionService.saveTransaction(debitTransaction);

            Transaction creditTransaction = new Transaction(amountTwoDecimals.doubleValue(), description + " | " + accountDestination, LocalDateTime.now(), TransactionType.CREDIT, destinationAccount.getBalance() + amount, true);
            destinationAccount.addTransaction(creditTransaction);
            transactionService.saveTransaction(creditTransaction);

            originAccount.setBalance(originAccount.getBalance() - amountTwoDecimals.doubleValue());
            accountService.saveAccount(originAccount);

            destinationAccount.setBalance(destinationAccount.getBalance() + amountTwoDecimals.doubleValue());
            accountService.saveAccount(destinationAccount);

            return new ResponseEntity<>("The transaction has been created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Unknown user", HttpStatus.UNAUTHORIZED);
    }
}
