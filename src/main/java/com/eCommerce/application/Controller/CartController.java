package com.eCommerce.application.Controller;

import com.eCommerce.application.Entity.Cart;
import com.eCommerce.application.Model.CartDTO;
import com.eCommerce.application.Model.CartItemsDTO;
import com.eCommerce.application.Repository.CartRepository;
import com.eCommerce.application.Service.CartService;
import com.eCommerce.application.Util.AuthUtils;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity){
        CartDTO cart = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(cart, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartDTO = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO> getCartByUser(){
        String userEmail = authUtils.getLoggedInEmail();
        Cart cart =cartRepository.findCartByEmail(userEmail);
        CartDTO cartDTO = cartService.getCartByUserDetails(userEmail, cart.getCartId());
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductQuantity(@PathVariable Long productId,
                                                               @PathVariable String operation){
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete")? -1: 1);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/products/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId){
        return new ResponseEntity<>(cartService.deleteProductFromCart(cartId,productId), HttpStatus.OK);
    }
}
