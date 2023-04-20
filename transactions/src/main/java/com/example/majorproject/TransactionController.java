package com.example.majorproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/create_transaction")
    public void createTransaction(TransactionRequest transactionRequest)
    {
        transactionService.createTranscation(transactionRequest);
    }
}
