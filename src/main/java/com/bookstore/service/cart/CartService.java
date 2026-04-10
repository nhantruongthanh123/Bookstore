package com.bookstore.service.cart;

import com.bookstore.dto.Cart.CartResponse;

public interface CartService {
    CartResponse getCart();
    CartResponse addToCart(Long bookId, Integer quantity);
    CartResponse updateCartItem(Long cartItemId, Integer quantity);
    CartResponse removeCartItem(Long cartItemId);
    CartResponse removeAllCartItems();
}
