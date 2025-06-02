package com.example.transactionServiceOfBank.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.transactionServiceOfBank.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>{

	@Query("""
		    SELECT t
		      FROM Transaction t
		     WHERE (t.fromAccount = :accountId OR t.toAccount   = :accountId)
		       AND t.createdAt BETWEEN :start AND :end
		  """)
		  List<Transaction> findStatement(
		      @Param("accountId") int accountId,
		      @Param("start") Timestamp start,
		      @Param("end")   Timestamp end);
		
}
