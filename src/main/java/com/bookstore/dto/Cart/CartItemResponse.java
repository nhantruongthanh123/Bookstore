package com.bookstore.dto.Cart;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long bookId,
        String title,
        String coverImage,
        BigDecimal price,
        Integer quantity,
        BigDecimal subTotal
) {}
