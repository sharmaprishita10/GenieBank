package com.example.accountServiceOfBank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.accountServiceOfBank.model.Account;
import com.example.accountServiceOfBank.payload.request.UpdateBalanceRequest;
import com.example.accountServiceOfBank.repository.AccountRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AccountService {

	@Autowired
	private AccountRepository accRepo;

	public String addAccount(Account account) {

		// Saving the account entity to get the unique id
		account = accRepo.save(account);

		// Append the unique id to the accNo and save again
		// Account number = 4 digit branch code + 11 digits of padded id with 0
		String paddedId = String.format("%011d", account.getId());
		String newAccountNumber = account.getAccountNumber() + paddedId;
		account.setAccountNumber(newAccountNumber);
		accRepo.save(account);
		return newAccountNumber;
	}

	public Account findIdByAccNum(String accNum) {
		
		return accRepo.findByAccountNumber(accNum);
	}

	public void updateBalance(UpdateBalanceRequest updateBalRequest) {
		
		Account account = accRepo.findById(updateBalRequest.getId()).get();
		double currentBal = account.getBalance();
		String updateType = updateBalRequest.getUpdateType();
		
		if(updateType.equals("Credit"))
		{
			account.setBalance(currentBal + updateBalRequest.getAmount());
		}
		else if(updateType.equals("Debit"))
		{
			account.setBalance(currentBal - updateBalRequest.getAmount());
		}
		
		accRepo.save(account);
	}

	public double getBalance(int id) {
		Account account = accRepo.findById(id).get();
		return account.getBalance();
	}

	public int findIdByCustId(int customerId) {
		return accRepo.findByCustomerId(customerId).getId();
	}

}
