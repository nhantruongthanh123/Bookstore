package com.bookstore.service.order;

import com.bookstore.dto.Order.OrderRequest;
import com.bookstore.dto.Order.OrderResponse;
import com.bookstore.dto.OrderItem.OrderItemRequest;
import com.bookstore.entity.*;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, OrderRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.shippingAddress());
        order.setPhoneNumber(request.phoneNumber());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            Book book = bookRepository.findById(itemReq.bookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            if (book.getQuantity() < itemReq.quantity()) {
                throw new RuntimeException("We don't have enough items in this order (" +  book.getQuantity() + " left)");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(itemReq.quantity());
            orderItem.setPrice(book.getPrice());
            orderItem.setOrder(order);

            order.getOrderItems().add(orderItem);

            BigDecimal subTotal = book.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            totalAmount = totalAmount.add(subTotal);

            book.setQuantity(book.getQuantity() - itemReq.quantity());
            bookRepository.save(book);
        }
        
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or you don't have permission to view it"));

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdAdmin(Long orderId) {
        return orderMapper.toOrderResponse(orderRepository.getReferenceById(orderId));
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId){
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or you don't have permission"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("You can only cancel orders in PENDING status. Current status is " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);

        for (OrderItem orderItem : order.getOrderItems()) {
            Book book = orderItem.getBook();
            book.setQuantity(book.getQuantity() + orderItem.getQuantity());

            bookRepository.save(book);
        }

        Order saveOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(saveOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistory(Long userId){
        List<Order> orders = orderRepository.findAllByUserIdOrderByOrderDateDesc(userId);
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }
    // List<OrderResponse> searchOrders(OrderSearchRequest searchParams)

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Pageable pageable){
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(orderMapper::toOrderResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == status) {
            return orderMapper.toOrderResponse(order);
        }

        if (status == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {
                Book book = item.getBook();
                book.setQuantity(book.getQuantity() + item.getQuantity());
            }
        }
        order.setStatus(status);

        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }
}
