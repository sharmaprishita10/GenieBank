package com.example.customerServiceOfBank.model;

import java.time.LocalDate;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Customer {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  

    @Column(unique = true, nullable = false)
    private String custId;  

    @Column(nullable = false)
    private String name;  

    @Column(unique = true)
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits.")  
    private String mobileNumber;  

    @Email(message = "Invalid email address")
    @Column(unique = true, nullable = false)
    private String email; 

    private LocalDate dob;

    private String address;  

    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be 12 digits.")  
    @Column(unique = true, nullable = false)
    private String aadhaarNumber;
    
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "PAN number is incorrect.")
    @Column(unique = true, nullable = false)
    private String panNumber;
    
    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDate createdOn;
    
    @Column(name = "is_netbanking_active", nullable = false)
    private boolean isNetbankingActive = false;
    
    @Column(name = "branch_id", nullable = false)
    private int branchId;
}
