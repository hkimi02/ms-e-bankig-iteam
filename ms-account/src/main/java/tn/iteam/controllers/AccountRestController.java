package tn.iteam.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.dtos.AccountContactInfoDto;
import tn.iteam.entities.Account;
import tn.iteam.services.AccountService;

import java.util.List;

@RestController
public class AccountRestController {

    private final AccountService accountService;
    private final AccountContactInfoDto contactInfoDto;
    @Value("${build.version}")
    private String buildVersion;
    public AccountRestController(AccountService accountService, AccountContactInfoDto contactInfoDto) {
        this.accountService = accountService;
        this.contactInfoDto = contactInfoDto;
    }

    // GET /accounts : tous les comptes (sans appel à ms-customer)
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAllAccounts());
    }
    @GetMapping("/build-version")
    public String getBuildInfo(){
        return buildVersion;
    }

    // GET /accounts/{id} : un compte + les détails de son client (via OpenFeign)
    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getOneAccount(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.fetchAccount(id));
    }

    // GET /accounts/customer/{customerId} : tous les comptes d'un client
    @GetMapping("/accounts/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.fetchAccountsByCustomerId(customerId));
    }

    @GetMapping("/accounts/getContactInfo")
    public AccountContactInfoDto getContactInfoDto(){
        return contactInfoDto;
    }

}
