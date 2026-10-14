package tn.iteam.services;

import tn.iteam.entities.Account;

import java.util.List;

public interface AccountService {

    List<Account> getAllAccounts();

    Account fetchAccount(String accountId);

    List<Account> fetchAccountsByCustomerId(Long customerId);
}
