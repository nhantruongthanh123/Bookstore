package com.bookstore.controller;

import com.bookstore.dto.Cart.AddToCartRequest;
import com.bookstore.dto.Cart.CartResponse;
import com.bookstore.dto.Cart.UpdateCartItemRequest;
import com.bookstore.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request.bookId(), request.quantity()));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(cartService.updateCartItem(cartItemId, request.quantity()));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(@PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeCartItem(cartItemId));
    }
}
