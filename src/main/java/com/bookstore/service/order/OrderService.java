package com.bookstore.service.order;

import com.bookstore.dto.Order.OrderRequest;
import com.bookstore.dto.Order.OrderResponse;
import com.bookstore.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(Long userId, OrderRequest request);
    OrderResponse getOrderById(Long orderId);
    OrderResponse cancelOrder(Long orderId, Long userId);
    OrderResponse getOrderHistory(Long userId);
    // List<OrderResponse> searchOrders(OrderSearchRequest searchParams)

    Page<OrderResponse> findAll(Pageable pageable);
    OrderResponse updateStatus(Long orderId, OrderStatus status);
}
