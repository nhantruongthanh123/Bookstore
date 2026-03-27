package com.bookstore.mapper;

import com.bookstore.dto.Order.OrderResponse;
import com.bookstore.dto.OrderItem.OrderItemResponse;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    OrderItemResponse toItemResponse(OrderItem orderItem);
}
