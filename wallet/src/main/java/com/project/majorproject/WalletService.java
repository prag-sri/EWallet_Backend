package com.project.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;

    KafkaTemplate<String,String> kafkaTemplate;

    @KafkaListener(topicPattern = "create_wallet",groupId = "test1234")
    public void createWallet(String message){
        //we will be sending username as message
        Wallet wallet= Wallet.builder().
                        userName(message).
                        balance(100).build();
        walletRepository.save(wallet);
    }

    @KafkaListener(topicPattern = "update_wallet",groupId = "test1234")
    public void updateWallet(String message) throws JsonProcessingException {
        //decoded back to JSONobject and extract information
        JSONObject jsonObject= objectMapper.convertValue(message,JSONObject.class);

        String fromUser= (String)jsonObject.get("fromUser");
        String toUser= (String)jsonObject.get("toUser");
        int amount= (Integer)jsonObject.get("amount");
        String id= (String)jsonObject.get("id");

        JSONObject returnObject= new JSONObject();
        returnObject.put("id",id);

        Wallet fromUserWallet= walletRepository.getWalletByUserName(fromUser);
        Wallet toUserWallet= walletRepository.getWalletByUserName(toUser);

        if(fromUserWallet.getBalance()>=amount)
        {
            //This is a successful transaction
            returnObject.put("status","SUCCESS");

            kafkaTemplate.send("update_transaction",objectMapper.writeValueAsString(returnObject));


            //Update the sender's and receiver's wallet
            fromUserWallet.setBalance(fromUserWallet.getBalance()-amount);
            toUserWallet.setBalance(toUserWallet.getBalance()+amount);

            walletRepository.save(fromUserWallet);
            walletRepository.save(toUserWallet);
        }
        else
        {
            returnObject.put("status","FAILED");
            kafkaTemplate.send("update_transaction",objectMapper.writeValueAsString(returnObject));

            //We do not update the wallets
        }
    }
}
