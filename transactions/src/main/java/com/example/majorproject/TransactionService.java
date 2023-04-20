package com.example.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    void createTranscation(TransactionRequest transactionRequest)
    {
        //First we will create a transaction and put its status as pending
        Transaction transaction= Transaction.builder().
                fromUser(transactionRequest.getFromUser()).
                toUser(transactionRequest.getToUser()).
                amount(transactionRequest.getAmount()).
                purpose(transactionRequest.getPurpose()).
                id(UUID.randomUUID().toString()).
                transactionDate(new Date()).
                transactionStatus(TransactionStatus.PENDING).build();

        transactionRepository.save(transaction);

        //Create that JSONobject
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("fromUser",transactionRequest.getFromUser());
        jsonObject.put("toUser",transactionRequest.getToUser());
        jsonObject.put("amount",transactionRequest.getAmount());
        jsonObject.put("id",transaction.getId());

        //Convert to String and send via Kafka to the wallet microservice
        String kafkaMessage= null;
        try {
            kafkaMessage = objectMapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send("update_wallet",kafkaMessage);

    }
}
