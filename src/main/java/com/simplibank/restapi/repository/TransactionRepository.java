package com.simplibank.restapi.repository;

import com.simplibank.restapi.entity.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByAccountNumberOrderByIdDesc(String accountNumber);
}