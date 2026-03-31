package com.bookstore.dto.Order;

import com.bookstore.dto.OrderItem.OrderItemResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String status,
        List<OrderItemResponse> orderItems
){}
