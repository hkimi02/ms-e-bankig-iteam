package tn.iteam.services;

import tn.iteam.dtos.CustomerDto;
import tn.iteam.entities.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();

    void createCustomer(CustomerDto customerDto);

    CustomerDto fetchCustomerById(Long id);

    CustomerDto fetchCustomerByMobileNumber(String mobileNumber);

    boolean updateAccount(CustomerDto customerDto);

    boolean deleteByMobileNumber(String mobileNumber);
}
