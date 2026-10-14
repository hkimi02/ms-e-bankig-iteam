package tn.iteam.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.iteam.dtos.CustomerContactInfoDto;
import tn.iteam.dtos.CustomerDto;
import tn.iteam.dtos.ResponseDto;
import tn.iteam.entities.Customer;
import tn.iteam.enums.CustomerResponseStatus;
import tn.iteam.services.CustomerService;

import java.util.List;

@RestController
public class CustomerRestController {

    private final CustomerService customerService;
    private final CustomerContactInfoDto contactInfoDto;
    @Value("${build.version}")
    private String buildVersion;

    public CustomerRestController(CustomerService customerService, CustomerContactInfoDto contactInfoDto) {
        this.customerService = customerService;
        this.contactInfoDto = contactInfoDto;
    }

    @GetMapping("/build-version")
    public String getBuildInfo() {
        return buildVersion;
    }

    @GetMapping("/customers/getContactInfo")
    public CustomerContactInfoDto getContactInfoDto() {
        return contactInfoDto;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> retrieveAllCustomers() {
        List<Customer> allCustomers = customerService.getAllCustomers();
        return ResponseEntity.status(HttpStatus.OK).body(allCustomers);
    }

    @PostMapping("/customers")
    public ResponseEntity<ResponseDto> addNewCustomer(@Valid @RequestBody CustomerDto customerDto) {
        customerService.createCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(CustomerResponseStatus.CREATED.getCode(),
                        CustomerResponseStatus.CREATED.getMessage()));
    }

    // "/customers/fetch" est déclaré avant "/customers/{id}" pour la lisibilité ;
    // Spring privilégie de toute façon le chemin littéral au chemin variable.
    @GetMapping("/customers/fetch")
    public ResponseEntity<CustomerDto> fetchCustomerByMobileNumber(@RequestParam String mobileNumber) {
        CustomerDto customerDto = customerService.fetchCustomerByMobileNumber(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDto> fetchCustomerDetails(@PathVariable Long id) {
        CustomerDto customerDto = customerService.fetchCustomerById(id);
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }

    @PutMapping("/customers")
    public ResponseEntity<ResponseDto> updateCustomerDetails(@Valid @RequestBody CustomerDto customerDto) {
        boolean isUpdated = customerService.updateAccount(customerDto);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(CustomerResponseStatus.SUCCESS.getCode(),
                            CustomerResponseStatus.SUCCESS.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(CustomerResponseStatus.SERVER_ERROR.getCode(),
                            CustomerResponseStatus.SERVER_ERROR.getMessage()));
        }
    }

    @DeleteMapping("/customers/delete")
    public ResponseEntity<ResponseDto> deleteCustomerDetails(@RequestParam String mobileNumber) {
        boolean isDeleted = customerService.deleteByMobileNumber(mobileNumber);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto(CustomerResponseStatus.SUCCESS.getCode(),
                            CustomerResponseStatus.SUCCESS.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(CustomerResponseStatus.SERVER_ERROR.getCode(),
                            CustomerResponseStatus.SERVER_ERROR.getMessage()));
        }
    }
}
