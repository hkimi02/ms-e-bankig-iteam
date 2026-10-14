package tn.iteam.services.impl;

import org.springframework.stereotype.Service;
import tn.iteam.entities.Account;
import tn.iteam.exceptions.AccountNotFoundException;
import tn.iteam.models.Customer;
import tn.iteam.proxy.CustomerOpenFeign;
import tn.iteam.repos.AccountRepository;
import tn.iteam.services.AccountService;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerOpenFeign customerOpenFeign;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerOpenFeign customerOpenFeign) {
        this.accountRepository = accountRepository;
        this.customerOpenFeign = customerOpenFeign;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account fetchAccount(String accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account", "accountId", accountId)
        );
        // appel à ms-customer (via Eureka + OpenFeign) pour compléter le compte avec son client
        account.setCustomer(fetchCustomer(account.getCustomerId()));
        return account;
    }

    @Override
    public List<Account> fetchAccountsByCustomerId(Long customerId) {
        // vérifie d'abord que le client existe dans ms-customer (sinon 404 renvoyé par le handler)
        Customer customer = fetchCustomer(customerId);
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        accounts.forEach(account -> account.setCustomer(customer));
        return accounts;
    }

    private Customer fetchCustomer(Long customerId) {
        Customer customer = customerOpenFeign.fetchCustomerDetails(customerId);
        // ms-customer renvoie un CustomerDto sans identifiant : on le complète
        customer.setCustomerId(customerId);
        return customer;
    }
}
