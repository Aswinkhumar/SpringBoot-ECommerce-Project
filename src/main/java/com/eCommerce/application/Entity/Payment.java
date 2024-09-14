package com.eCommerce.application.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Order order;

    @NotBlank
    @Size(min = 4, message = "Payment Method name should have at-least 4 characters.")
    private String paymentMethod;

    private String pgName;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;

    public Payment(String paymentMethod, String pgPaymentId, String pgStatus, String pgName, String pgResponseMessage) {
        this.paymentMethod = paymentMethod;
        this.pgPaymentId = pgPaymentId;
        this.pgStatus = pgStatus;
        this.pgName = pgName;
        this.pgResponseMessage = pgResponseMessage;
    }
}
