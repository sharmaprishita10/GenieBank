package com.example.transactionServiceOfBank.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Transaction {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; 
	
	@Column(nullable = true)
	private int fromAccount;
	
	@Column(nullable = true)
	private int toAccount;
	
	@Column(nullable = false)
	private double amount;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
 
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy; 		// username (empId or custId)
    
    @Enumerated(EnumType.STRING)
    private TransferType transferType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModeOfTransaction modeOfTransaction;
}
