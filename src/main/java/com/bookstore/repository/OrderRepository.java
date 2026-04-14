package com.bookstore.repository;

import com.bookstore.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserIdOrderByOrderDateDesc(Long userId);
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.book", "user"})
    List<Order> findByOrderDateBetweenOrderByOrderDateAsc(LocalDateTime startDate, LocalDateTime endDate);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.book", "user"})
    List<Order> findTop3ByOrderByOrderDateDesc();

    @Query("""
            select oi.book.id
            from OrderItem oi
            where oi.order.status <> com.bookstore.entity.OrderStatus.CANCELLED
            group by oi.book.id
            order by sum(oi.quantity) desc
            """)
    List<Long> findTopSellerBookIds(Pageable pageable);

    @Query("""
            select oi.book.id, sum(oi.quantity)
            from OrderItem oi
            where oi.order.status <> com.bookstore.entity.OrderStatus.CANCELLED
              and oi.order.orderDate between :startDate and :endDate
            group by oi.book.id
            order by sum(oi.quantity) desc
            """)
    List<Object[]> findTopSellerBookStatsInRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
