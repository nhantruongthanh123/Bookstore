package com.bookstore.dto.Order;

import com.bookstore.dto.OrderItem.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record OrderRequest(
        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        Set<OrderItemRequest> items
) {}
