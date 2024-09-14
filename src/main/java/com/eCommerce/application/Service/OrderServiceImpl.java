package com.eCommerce.application.Service;

import com.eCommerce.application.Entity.*;
import com.eCommerce.application.Exceptions.APIException;
import com.eCommerce.application.Exceptions.ResourceNotFoundException;
import com.eCommerce.application.Model.OrderDTO;
import com.eCommerce.application.Model.OrderItemDTO;
import com.eCommerce.application.Repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO orderProduct(String userEmail, String paymentMethod, String pgPaymentId, Long addressId, String pgName, String pgStatus, String pgResponseMessage) {
        // Validating the Cart for the current user
        Cart cart = cartRepository.findCartByEmail(userEmail);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "emailId", userEmail);
        }

        // Validating the address with the given Id
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Creating the payment object with the given information
        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgName, pgResponseMessage);

        // Creating the Order object.
        Order order = new Order();
        order.setEmail(userEmail);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("Order Placed Successfully!");
        order.setTotalAmount(cart.getTotalPrice());

        // Mapping the Order against the payment and saving it in DB
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        // Extracting the cartItems from the cart and saving them as OrderItems in the DB
        List<CartItem> cartItems = cart.getCartItems();

        List<OrderItem> orderItemList = new ArrayList<>();

        for(CartItem item : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setDiscount(item.getDiscount());
            orderItem.setOrderedProductPrice(item.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItemList.add(orderItem);
        }

        orderItemList = orderItemRepository.saveAll(orderItemList);
        savedOrder.setOrderItems(orderItemList);
        // Updating the stock quantity against the order purchase.
        cart.getCartItems().forEach(cartItem -> {
            int cartQuantity = cartItem.getQuantity();
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartQuantity);
            productRepository.save(product);
            cartService.deleteProductFromCart(cart.getCartId(), cartItem.getProduct().getProductId());
        });

        // Returning the OrderDTO
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItemList.forEach(orderItem -> orderDTO.getOrderItems().add(modelMapper.map(orderItem, OrderItemDTO.class)));
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
