package com.eCommerce.application.Controller;

import com.eCommerce.application.Model.OrderDTO;
import com.eCommerce.application.Model.OrderRequestDTO;
import com.eCommerce.application.Service.OrderService;
import com.eCommerce.application.Util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/user/payment/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProduct(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO){
        String userEmail = authUtils.getLoggedInEmail();

        OrderDTO orderDTO = orderService.orderProduct(
                userEmail,
                paymentMethod,
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getAddressId(),
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
