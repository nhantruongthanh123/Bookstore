package com.bookstore.dto.Cart;

public record AddToCartRequest(
        Long bookId,
        Integer quantity
) {}
