package com.example.majorproject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="transaction")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Transaction {

    @Id
    private String transactionId;
    private String fromUser;
    private String toUser;
    private int amount;
    @Enumerated(value = EnumType.STRING)
    private TransactionStatus transactionStatus;
    private Date transactionDate;
    private String purpose;
}
