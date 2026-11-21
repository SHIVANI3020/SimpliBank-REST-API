package com.simplibank.restapi.service;
import java.util.List;

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
        public List<Transaction> getHistory(String accountNumber) {
        getAccount(accountNumber);   // throws 404 if the account doesn't exist
        return transactionRepository.findByAccountNumberOrderByIdDesc(accountNumber);
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
        @Transactional
    public Account transfer(String fromNumber, String toNumber, BigDecimal amount) {
        validateAmount(amount);
        if (fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }
        Account from = getAccount(fromNumber);
        Account to = getAccount(toNumber);
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Available balance: Rs." + from.getBalance());
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);
        transactionRepository.save(new Transaction(
                fromNumber, "TRANSFER_OUT", amount, from.getBalance(), "Transfer"));
        transactionRepository.save(new Transaction(
                toNumber, "TRANSFER_IN", amount, to.getBalance(), "Transfer"));
        return from;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
    }
}