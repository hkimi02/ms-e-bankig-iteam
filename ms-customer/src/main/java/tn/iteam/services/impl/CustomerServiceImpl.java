package tn.iteam.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iteam.dtos.CustomerDto;
import tn.iteam.entities.Customer;
import tn.iteam.exceptions.CustomerAlreadyExistsException;
import tn.iteam.exceptions.CustomerNotFoundException;
import tn.iteam.mappers.CustomerMapper;
import tn.iteam.repos.CustomerRepository;
import tn.iteam.services.CustomerService;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            throw new CustomerNotFoundException("Customer", "list", "No customer found");
        }
        return customers;
    }

    @Override
    public void createCustomer(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer =
                customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer", "mobileNumber",
                    customerDto.getMobileNumber());
        }
        customerRepository.save(customer);
    }

    @Override
    public CustomerDto fetchCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer", "customerId", id.toString())
        );
        return CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
    }

    @Override
    public CustomerDto fetchCustomerByMobileNumber(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new CustomerNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        return CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
    }

    @Override
    @Transactional
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        if (customerDto != null) {
            Customer customer = customerRepository.findByMobileNumber(customerDto.getMobileNumber()).orElseThrow(
                    () -> new CustomerNotFoundException("Customer", "mobileNumber", customerDto.getMobileNumber())
            );
            Customer savedCustomer = CustomerMapper.mapToCustomer(customerDto, new Customer());
            // on réutilise l'id existant pour que save() fasse un UPDATE et non un INSERT
            savedCustomer.setCustomerId(customer.getCustomerId());
            customerRepository.save(savedCustomer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    @Transactional
    public boolean deleteByMobileNumber(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new CustomerNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }
}
