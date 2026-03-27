package com.bookstore.dto.Order;

import com.bookstore.dto.OrderItem.OrderItemRequest;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String shippingAddress;
    private String phoneNumber;
    private List<OrderItemRequest> items;
}
