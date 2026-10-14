package tn.iteam;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import tn.iteam.dtos.AccountContactInfoDto;
import tn.iteam.entities.Account;
import tn.iteam.enums.AccountType;
import tn.iteam.repos.AccountRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(AccountContactInfoDto.class)
public class MsAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAccountApplication.class, args);
        System.out.println("****Account microservice started successfully*****");
    }

    @Bean
    public CommandLineRunner initData(AccountRepository accountRepository) {
        return args -> {
            // insère 3 comptes de test au démarrage (uniquement si la table est vide)
            if (accountRepository.count() > 0) {
                return;
            }
            List<Account> accounts = List.of(
                    Account.builder().accountId(UUID.randomUUID().toString())
                            .currency("TND").balance(Math.random() * 99999)
                            .createdAt(LocalDate.now()).type(AccountType.CURRENT)
                            .customerId(1L).build(),
                    Account.builder().accountId(UUID.randomUUID().toString())
                            .currency("EUR").balance(Math.random() * 99999)
                            .createdAt(LocalDate.now()).type(AccountType.SAVING)
                            .customerId(1L).build(),
                    Account.builder().accountId(UUID.randomUUID().toString())
                            .currency("USD").balance(Math.random() * 99999)
                            .createdAt(LocalDate.now()).type(AccountType.CURRENT)
                            .customerId(2L).build()
            );
            accountRepository.saveAll(accounts);
        };
    }
}
