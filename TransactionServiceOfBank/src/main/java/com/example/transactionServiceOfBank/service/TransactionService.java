package com.example.transactionServiceOfBank.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.transactionServiceOfBank.dto.StatementEntry;
import com.example.transactionServiceOfBank.model.Transaction;
import com.example.transactionServiceOfBank.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TransactionService {

	@Autowired
	private TransactionRepository tranxRepo;
	
	public void addTransaction(Transaction transaction) {
		
		tranxRepo.save(transaction);
	}

	public List<StatementEntry> getStatement(int accountId, Timestamp start, Timestamp end) {
		return tranxRepo.findStatement(accountId, start, end).stream()
		        .map(t -> {
		          String type = (t.getFromAccount() == accountId) ? "DEBIT" : "CREDIT";
		          return new StatementEntry(t.getCreatedAt(), t.getAmount(), type);
		        })
		        .collect(Collectors.toList());
	}

}
