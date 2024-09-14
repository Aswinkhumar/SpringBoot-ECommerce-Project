package com.eCommerce.application.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @NotBlank
    @Size(min = 4, message = "StreetName should be at least 4 characters.")
    private String streetName;

    @NotBlank
    @Size(min = 4, message = "Building name should be at least 4 characters.")
    private String buildingName;

    @NotBlank
    @Size(min = 3, message = "City name should be at least 3 characters.")
    private String city;

    @NotBlank
    @Size(min = 2, message = "State name should be at least 2 characters.")
    private String state;

    @NotBlank
    @Size(min = 2, message = "Country name should be at least 2 characters.")
    private String country;

    @NotBlank
    @Size(min = 6, message = "pinCode should be at least 6 characters.")
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
