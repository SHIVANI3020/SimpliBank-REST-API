package com.simplibank.restapi.controller;

import com.simplibank.restapi.entity.Account;
import com.simplibank.restapi.service.AccountService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountNumber}/balance")
    public BalanceResponse getBalance(@PathVariable String accountNumber) {
        return toResponse(accountService.getAccount(accountNumber));
    }

    @PostMapping("/{accountNumber}/deposit")
    public BalanceResponse deposit(@PathVariable String accountNumber,
                                   @RequestBody AmountRequest request) {
        return toResponse(accountService.deposit(accountNumber, request.amount()));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public BalanceResponse withdraw(@PathVariable String accountNumber,
                                    @RequestBody AmountRequest request) {
        return toResponse(accountService.withdraw(accountNumber, request.amount()));
    }

    private BalanceResponse toResponse(Account a) {
        return new BalanceResponse(a.getAccountNumber(), a.getAccountHolder(), a.getBalance());
    }

    public record AmountRequest(BigDecimal amount) { }

    public record BalanceResponse(String accountNumber, String accountHolder, BigDecimal balance) { }
}