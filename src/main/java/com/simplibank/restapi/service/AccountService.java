package com.simplibank.restapi.service;

import com.simplibank.restapi.entity.Account;
import com.simplibank.restapi.entity.Transaction;
import com.simplibank.restapi.exception.AccountNotFoundException;
import com.simplibank.restapi.exception.InsufficientFundsException;
import com.simplibank.restapi.repository.AccountRepository;
import com.simplibank.restapi.repository.TransactionRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);
        Account account = getAccount(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        transactionRepository.save(new Transaction(
                accountNumber, "DEPOSIT", amount, account.getBalance(), "Other"));
        return account;
    }

    @Transactional
    public Account withdraw(String accountNumber, BigDecimal amount) {
        validateAmount(amount);
        Account account = getAccount(accountNumber);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Available balance: Rs." + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        transactionRepository.save(new Transaction(
                accountNumber, "WITHDRAW", amount, account.getBalance(), "Other"));
        return account;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
    }
}