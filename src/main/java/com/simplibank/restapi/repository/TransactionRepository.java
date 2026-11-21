package com.simplibank.restapi.repository;

import com.simplibank.restapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> { }