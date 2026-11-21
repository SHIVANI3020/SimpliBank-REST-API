package com.simplibank.restapi.repository;

import com.simplibank.restapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> { }