package tn.iteam.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.iteam.models.Customer;

import java.util.List;

/**
 * Client REST déclaratif vers ms-customer.
 * "MS-CUSTOMER" est le nom sous lequel ms-customer est enregistré dans Eureka :
 * l'adresse réelle (host:port) est résolue par le load balancer, rien n'est codé en dur.
 */
@FeignClient(name = "MS-CUSTOMER")
public interface CustomerOpenFeign {

    @GetMapping("/customers")
    List<Customer> retrieveAllCustomers();

    @GetMapping("/customers/{id}")
    Customer fetchCustomerDetails(@PathVariable("id") Long id);
}
