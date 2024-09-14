package com.eCommerce.application.Service;

import com.eCommerce.application.Model.OrderDTO;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO orderProduct(String userEmail, String paymentMethod, String pgPaymentId, Long addressId, String pgName, String pgStatus, String pgResponseMessage);
}
