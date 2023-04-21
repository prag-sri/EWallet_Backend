package com.example.majorproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {

    //Kafka Consumer Properties: Kafka Consumer will try to be the consumer of this message
    @Bean
    Properties kafkaProps(){

        Properties properties = new Properties();

        //Consumer Properties
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"test1234");

        return properties;
    }

    @Bean
    ConsumerFactory<String,String> getConsumerFactory(){
        return new DefaultKafkaConsumerFactory(kafkaProps());
    }


    //This is only for consumers bcz they have to listen simultaneous...so this property needs to be there
    @Bean
    ConcurrentKafkaListenerContainerFactory<String,String> concurrentKafkaListenerContainerFactory(){

        ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(getConsumerFactory());
        return concurrentKafkaListenerContainerFactory;
    }

    @Bean
    ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }

    @Bean
    SimpleMailMessage getSimpleMessage(){
        return new SimpleMailMessage();
    }

    @Bean
    JavaMailSender getJavaMailSender(){

        JavaMailSender javaMailSender= new JavaMailSenderImpl();

        ((JavaMailSenderImpl) javaMailSender).setHost("smtp.gmail.com");
        ((JavaMailSenderImpl) javaMailSender).setPort(587);

        ((JavaMailSenderImpl) javaMailSender).setUsername("backendacciojob@gmail.com");
        ((JavaMailSenderImpl) javaMailSender).setPassword("Accio1234.");

        Properties properties = ((JavaMailSenderImpl) javaMailSender).getJavaMailProperties();

        properties.put("mail.transport.protocol","smtp");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.stattls.enable","true");
        properties.put("mail.debug","true");

        return javaMailSender;
    }

}
