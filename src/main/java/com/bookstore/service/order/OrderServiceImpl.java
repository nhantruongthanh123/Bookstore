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

import java.time.LocalDateTime;

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
        order.setShippingAddress(request.getShippingAddress());
        order.setPhoneNumber(request.getPhoneNumber());

        double totalAmount = 0.0;

        for (OrderItemRequest itemReq : request.getItems()) {
            Book book = bookRepository.findById(itemReq.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            if (book.getQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("We don't have enough items in this order (" +  itemReq.getQuantity() + " left)");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(book.getPrice()); // Chốt giá tại thời điểm mua
            orderItem.setOrder(order);

            order.getOrderItems().add(orderItem);

            double subTotal = book.getPrice() * itemReq.getQuantity();
            totalAmount += subTotal;

            book.setQuantity(book.getQuantity() - itemReq.getQuantity());
            bookRepository.save(book);
        }
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse getOrderById(Long orderId){
        return null;
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId){
        return null;
    }

    @Override
    @Transactional
    public OrderResponse getOrderHistory(Long userId){
        return null;
    }
    // List<OrderResponse> searchOrders(OrderSearchRequest searchParams)

    @Override
    @Transactional
    public Page<OrderResponse> findAll(Pageable pageable){
        return null;
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status){
        return null;
    }

}
