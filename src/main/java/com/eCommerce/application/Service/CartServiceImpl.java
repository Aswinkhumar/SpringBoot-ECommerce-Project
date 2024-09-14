package com.eCommerce.application.Service;

import com.eCommerce.application.Entity.Cart;
import com.eCommerce.application.Entity.CartItem;
import com.eCommerce.application.Entity.Product;
import com.eCommerce.application.Exceptions.APIException;
import com.eCommerce.application.Exceptions.ResourceNotFoundException;
import com.eCommerce.application.Model.CartDTO;
import com.eCommerce.application.Model.ProductDTO;
import com.eCommerce.application.Repository.CartItemRepository;
import com.eCommerce.application.Repository.CartRepository;
import com.eCommerce.application.Repository.ProductRepository;
import com.eCommerce.application.Util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtils authUtils;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cart.getCartId(), productId);

        if(cartItem != null){
            throw new APIException("Product"  + product.getProductName() + " already exists in you cart.");
        }
        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName()+" is out of Stock. Please try after sometime!");
        }
        if(product.getQuantity() < quantity){
            throw new APIException("Only "+ product.getQuantity()+ " are available. Please make an order equal to or less than this quantity");
        }
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItemList = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItemList.stream().map(item ->{
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();

        if(carts == null){
            throw new APIException("No cart Exists!");
        }

        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
                List<ProductDTO> productDTOS = cart.getCartItems().stream()
                    .map(product -> {
                        ProductDTO productDTO = modelMapper.map(product.getProduct(), ProductDTO.class);
                        productDTO.setQuantity(product.getQuantity());
                        return productDTO;
                    }).toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getCartByUserDetails(String userEmail, Long cartId) {

        Cart cart = cartRepository.findByEmailIdAndCartId(userEmail, cartId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "CartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(item -> modelMapper.map(item.getProduct(), ProductDTO.class))
                .toList();
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String userEmail = authUtils.getLoggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(userEmail);
        Long cartId = userCart.getCartId();

        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "CartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId", productId));

        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName()+" is out of Stock. Please try after sometime!");
        }
        if(product.getQuantity() < quantity){
            throw new APIException("Only "+ product.getQuantity()+ " are available. Please make an order equal to or less than this quantity");
        }

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);

        if(cartItem == null){
            throw new APIException("Product"+ product.getProductName()+ "is not found");
        }

        int newQuantity = cartItem.getQuantity() + quantity;
        if(newQuantity < 0){
            throw new APIException("Product quantity cannot be in negative!");
        }
        if(newQuantity == 0){
            deleteProductFromCart(cartId,productId);
        }
        else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            existingCart.setTotalPrice(existingCart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(existingCart);
        }
        CartItem updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }
        CartDTO cartDTO = modelMapper.map(existingCart, CartDTO.class);

        List<CartItem> cartItems = existingCart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });
        
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);
        if(cartItem == null){
            throw new ResourceNotFoundException("Product","ProductId", productId);
        }
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(productId,cartId);

        return "Product " + cartItem.getProduct().getProductName() + "has been deleted successfully!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","cartId", cartId));

        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);
        if(cartItem == null){
            throw new ResourceNotFoundException("Product","ProductId", productId);
        }

        double cartTotalPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartTotalPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.save(cartItem);
    }

    private Cart createCart() {
        Cart existingCart = cartRepository.findCartByEmail(authUtils.getLoggedInEmail());

        if(existingCart!=null){
            return existingCart;
        }

        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtils.getLogedInUser());

        return cartRepository.save(newCart);
    }
}
