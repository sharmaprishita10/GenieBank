package com.example.accountServiceOfBank.model;


import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Account {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; 
	
	@Column(unique = true, nullable = false)
    private String accountNumber;
	
	@Column(nullable = false)
    private int customerId;  
	
	@Column(name = "balance", nullable = false)
    private double balance = 0.0;
	
	@Column(name = "is_active", nullable = false)
    private boolean isActive = true;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
 
    @Column(name = "created_by", nullable = false, updatable = false)
    private int createdBy;   

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(nullable = true)
    private int updatedBy;
}
