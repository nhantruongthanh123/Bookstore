package com.bookstore.dto.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long bookId,
        String bookTitle,
        Integer quantity,
        BigDecimal price
) {}
