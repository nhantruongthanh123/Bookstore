package com.bookstore.service.order;

import com.bookstore.dto.Order.OrderRequest;
import com.bookstore.dto.Order.OrderResponse;
import com.bookstore.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse placeOrder(Long userId, OrderRequest request);
    OrderResponse getOrderById(Long orderId, Long userId);
    OrderResponse getOrderByIdAdmin(Long orderId);
    OrderResponse cancelOrder(Long orderId, Long userId);
    Page<OrderResponse> getOrderHistory(Long userId, Pageable pageable);
    // List<OrderResponse> searchOrders(OrderSearchRequest searchParams)

    Page<OrderResponse> findAll(Pageable pageable);
    OrderResponse updateStatus(Long orderId, OrderStatus status);
}
