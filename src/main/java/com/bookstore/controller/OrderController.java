package com.bookstore.controller;

import com.bookstore.dto.Order.OrderRequest;
import com.bookstore.dto.Order.OrderResponse;
import com.bookstore.entity.OrderStatus;
import com.bookstore.security.AuthenticationUtil;
import com.bookstore.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bookstore.security.AuthenticationUtil.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody @Valid OrderRequest request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Long userId = AuthenticationUtil.getCurrentUserId();
        return ResponseEntity.ok(orderService.getOrderById(id, userId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Long userId = AuthenticationUtil.getCurrentUserId();
        return ResponseEntity.ok(orderService.cancelOrder(id, userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<OrderResponse> getOrderByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderByIdAdmin(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}

