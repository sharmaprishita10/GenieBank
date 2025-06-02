package com.example.accountServiceOfBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.accountServiceOfBank.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

	Account findByAccountNumber(String accNum);

	Account findByCustomerId(int customerId);

}
