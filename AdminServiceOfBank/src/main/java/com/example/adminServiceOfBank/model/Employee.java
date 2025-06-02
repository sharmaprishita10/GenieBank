package com.example.adminServiceOfBank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Employee{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  

    @Column(unique = true, nullable = false)
    private String empId;  

    @Column(nullable = false)
    private String name;  

    @Column(unique = true)
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be of 10 digits")  // Validation for mobile number (10 digits)
    private String mobileNumber;  

    @Email(message = "Invalid email address")
    @Column(unique = true, nullable = false)
    private String email; 

    @Temporal(TemporalType.DATE)
    private Date dob;

    private String address;  

    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be of 12 digits")  // Validation for Aadhaar number (12 digits)
    @Column(unique = true, nullable = false)
    private String aadhaarNumber;  
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", referencedColumnName = "id")
    private Branch branch;
    
    @ManyToMany
    @JoinTable(
        name = "employee_roles", // Join table name
        joinColumns = {@JoinColumn(name = "employee_id")}, // Foreign key to Employee
        inverseJoinColumns = {@JoinColumn(name = "role_id")} // Foreign key to Role
    )
    private List<Role> roles; // List of roles for this employee

}
