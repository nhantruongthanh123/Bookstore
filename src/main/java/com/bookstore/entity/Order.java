package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private LocalDateTime orderDate;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Column
    private String shippingAddress;
    @Column
    private String phoneNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return id != null && id.equals(order.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
