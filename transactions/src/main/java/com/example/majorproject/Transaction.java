package com.example.majorproject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="transactions")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String fromUser;
    private String toUser;
    private double amount;
    @Enumerated(value = EnumType.STRING)
    private TransactionStatus transactionStatus;
    private Date transactionDate;
    private String purpose;
}
