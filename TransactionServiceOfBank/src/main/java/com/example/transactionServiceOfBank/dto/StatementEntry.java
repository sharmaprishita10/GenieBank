package com.example.transactionServiceOfBank.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class StatementEntry {

	private Timestamp transactionDate;
	private double amount;
	private String type;
}
