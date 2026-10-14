package tn.iteam.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.iteam.enums.AccountType;
import tn.iteam.models.Customer;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {

    @Id
    private String accountId;

    private double balance;

    private LocalDate createdAt;

    private String currency;

    // stocké en texte (CURRENT, SAVING...) plutôt qu'en numéro d'ordre
    @Enumerated(EnumType.STRING)
    private AccountType type;

    // identifiant du client dans ms-customer (pas de clé étrangère entre microservices)
    private Long customerId;

    // rempli à la volée via OpenFeign, non persisté
    @Transient
    private Customer customer;
}
