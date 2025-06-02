package com.example.customerServiceOfBank.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.customerServiceOfBank.payload.response.ApiResponse;
import com.example.customerServiceOfBank.payload.request.*;
import com.example.customerServiceOfBank.clients.AccountClient;
import com.example.customerServiceOfBank.clients.TransactionClient;
import com.example.customerServiceOfBank.model.Customer;
import com.example.customerServiceOfBank.repository.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository custRepo;
	
	@Autowired
	private AccountClient accountClient;
	
	@Autowired
	private TransactionClient transactionClient;
	
	public String addCustomer(Customer customer) {
		// Saving the customer entity to get the unique id
		customer = custRepo.save(customer);
		
		// Append the unique id to the custId and save again
		customer.setCustId(customer.getCustId() + customer.getId());
		custRepo.save(customer);
		
		return customer.getCustId();
	}

	public Customer findCustomerByCustId(String custId) {
		
		return custRepo.findByCustId(custId);
	}

	public boolean activateNetbanking(String username) {
		
		Customer customer = custRepo.findByCustId(username);
		if(!customer.isNetbankingActive())
		{
			customer.setNetbankingActive(true);
			custRepo.save(customer);
			return true;
		}
		else
		{
			return false;
		}
	}

	public String transferAmount(String username, TransferRequest transferRequest) {
		
		String message;
		Customer customer = custRepo.findByCustId(username);
		if(customer.isNetbankingActive())
		{
			ApiResponse response = accountClient.getIdByCustId(customer.getId());
			int fromAccountId = Integer.parseInt(response.getData().toString());
			
			response = accountClient.getBalance(fromAccountId);
			double availBal = Double.parseDouble(response.getData().toString());
			
			double amount = transferRequest.getAmount();
			if((availBal - amount) >= 0)
			{
				response = accountClient.getIdByAccNum(transferRequest.getAccountNumber());
				if(response.getData() == null)
				{
					return null;
				}
				
				int toAccountId = Integer.parseInt(response.getData().toString());
				
				if(fromAccountId != toAccountId)
				{
					TransactionRequest transactionRequest = new TransactionRequest(fromAccountId, toAccountId, amount, "TRANSFER", username, "ONLINE");
					transactionClient.addTransaction(transactionRequest);
					
					UpdateBalanceRequest updateBalRequest = new UpdateBalanceRequest(fromAccountId, "Debit", amount);
					accountClient.updateBalance(updateBalRequest);
					
					updateBalRequest = new UpdateBalanceRequest(toAccountId, "Credit", amount);
					accountClient.updateBalance(updateBalRequest);
					message = "Amount transfered successfully!";
				}
				else
				{
					message = "Transfer Failed: Sender and recipient accounts must be different.";
				}
				
			}
			else
			{
				message = "Transfer failed due to insufficient balance.";
			}
		}
		else 
		{
			message = "Netbanking is not active.";
		}
		
		return message;
	}

	public Object getStatement(String username, LocalDate from, LocalDate to) {
		
		Customer customer = custRepo.findByCustId(username);
		ApiResponse response = accountClient.getIdByCustId(customer.getId());
		int accountId = Integer.parseInt(response.getData().toString());
		
		response = transactionClient.getStatement(accountId, from, to);
		return response.getData();
	}

	public double getBalance(String username) {
		Customer customer = custRepo.findByCustId(username);
		
		ApiResponse response = accountClient.getIdByCustId(customer.getId());
		int accountId = Integer.parseInt(response.getData().toString());
		
		response = accountClient.getBalance(accountId);
		double availBal = Double.parseDouble(response.getData().toString());
		
		return availBal;
	}  

}
