package com.simplibank.restapi.controller;

import com.simplibank.restapi.entity.Account;
import com.simplibank.restapi.service.AccountService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceResponse createAccount(@RequestBody CreateAccountRequest request) {
        return toResponse(accountService.createAccount(
                request.accountHolder(), request.pin(), request.initialDeposit()));
    }

    @GetMapping("/{accountNumber}/balance")
    public BalanceResponse getBalance(@PathVariable String accountNumber) {
        return toResponse(accountService.getAccount(accountNumber));
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionResponse> getHistory(@PathVariable String accountNumber) {
        return accountService.getHistory(accountNumber).stream()
                .map(t -> new TransactionResponse(t.getId(), t.getType(), t.getAmount(),
                        t.getBalanceAfter(), t.getTxnTime(), t.getCategory()))
                .toList();
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

    @PostMapping("/transfer")
    public BalanceResponse transfer(@RequestBody TransferRequest request) {
        return toResponse(accountService.transfer(
                request.fromAccount(), request.toAccount(), request.amount()));
    }

    private BalanceResponse toResponse(Account a) {
        return new BalanceResponse(a.getAccountNumber(), a.getAccountHolder(), a.getBalance());
    }

    public record CreateAccountRequest(String accountHolder, String pin, BigDecimal initialDeposit) { }

    public record AmountRequest(BigDecimal amount) { }

    public record BalanceResponse(String accountNumber, String accountHolder, BigDecimal balance) { }

    public record TransferRequest(String fromAccount, String toAccount, BigDecimal amount) { }

    public record TransactionResponse(Integer id, String type, BigDecimal amount,
                                      BigDecimal balanceAfter, LocalDateTime txnTime,
                                      String category) { }
}