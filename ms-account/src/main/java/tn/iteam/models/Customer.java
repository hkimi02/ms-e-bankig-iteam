package tn.iteam.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modèle (pas une entité) : représente un client renvoyé par ms-customer via OpenFeign.
 * Il n'est jamais stocké dans la base de ms-account.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Customer {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
}
