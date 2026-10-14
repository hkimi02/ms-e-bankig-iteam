package tn.iteam;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tn.iteam.dtos.CustomerContactInfoDto;
import tn.iteam.entities.Customer;
import tn.iteam.repos.CustomerRepository;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(CustomerContactInfoDto.class)
public class MsCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCustomerApplication.class, args);
        System.out.println("****Customer microservice started successfully*****");
    }

    @Bean
    public CommandLineRunner initData(CustomerRepository customerRepository) {
        return args -> {
            // insère 3 clients de test au démarrage (uniquement si la table est vide)
            if (customerRepository.count() > 0) {
                return;
            }
            List<Customer> customers = List.of(
                    Customer.builder().firstName("Tarek").lastName("Zemzmi")
                            .email("tarek@zemzmi.com").mobileNumber("12345678").build(),
                    Customer.builder().firstName("Linda").lastName("Nassri")
                            .email("linda@nassri.com").mobileNumber("12345679").build(),
                    Customer.builder().firstName("Wassim").lastName("Achouri")
                            .email("wassim@achouri.com").mobileNumber("12345677").build()
            );
            customerRepository.saveAll(customers);
        };
    }
}
