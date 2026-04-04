package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.entity.OrderStatus;
import com.bookstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByUserIdOrderByOrderDateDesc_ShouldReturnOrdersSortedByDate() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("password");
        user.setFullName("Test User");
        user.setPhoneNumber("0123456789");
        User savedUser = entityManager.persist(user);

        Order order1 = new Order();
        order1.setUser(savedUser);
        order1.setOrderDate(LocalDateTime.now().minusDays(2));
        order1.setTotalAmount(new BigDecimal("100.00"));
        order1.setShippingAddress("Address 1");
        order1.setStatus(OrderStatus.PENDING);
        order1.setPhoneNumber(user.getPhoneNumber());
        entityManager.persist(order1);

        Order order2 = new Order();
        order2.setUser(savedUser);
        order2.setOrderDate(LocalDateTime.now().minusDays(1));
        order2.setTotalAmount(new BigDecimal("200.00"));
        order2.setShippingAddress("Address 2");
        order2.setStatus(OrderStatus.DELIVERED);
        order2.setPhoneNumber(user.getPhoneNumber());
        entityManager.persist(order2);

        entityManager.flush();
        entityManager.clear();

        List<Order> orders = orderRepository.findAllByUserIdOrderByOrderDateDesc(savedUser.getId());

        assertThat(orders).isNotEmpty();
        assertThat(orders).hasSize(2);

        assertThat(orders.get(0).getId()).isEqualTo(order2.getId());
        assertThat(orders.get(1).getId()).isEqualTo(order1.getId());
    }

    @Test
    void findByIdAndUserId_ShouldReturnOrder_WhenOrderBelongsToUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("password");
        user.setFullName("Test User");
        user.setPhoneNumber("0123456789");
        User savedUser = entityManager.persist(user);

        Order order = new Order();
        order.setUser(savedUser);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("150.00"));
        order.setShippingAddress("Address");
        order.setStatus(OrderStatus.DELIVERED);
        order.setPhoneNumber(user.getPhoneNumber());
        Order savedOrder = entityManager.persist(order);

        entityManager.flush();
        entityManager.clear();

        Optional<Order> retrievedOrder = orderRepository.findByIdAndUserId(savedOrder.getId(), savedUser.getId());

        assertThat(retrievedOrder)
                .isPresent()
                .map(Order::getId)
                .contains(savedOrder.getId());
    }

    @Test
    void findByIdAndUserId_ShouldReturnEmpty_WhenOrderBelongsToAnotherUser() {
        User user1 = new User();
        user1.setUsername("testuser1");
        user1.setEmail("test1@gmail.com");
        user1.setPassword("password");
        user1.setFullName("Test User 1");
        user1.setPhoneNumber("0123456789");
        User savedUser1 = entityManager.persist(user1);

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("test2@gmail.com");
        user2.setPassword("password");
        user2.setFullName("Test User 2");
        user2.setPhoneNumber("0123456789");
        User savedUser2 = entityManager.persist(user2);

        Order order = new Order();
        order.setUser(savedUser1);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("150.00"));
        order.setShippingAddress("Address");
        order.setStatus(OrderStatus.DELIVERED);
        order.setPhoneNumber(user1.getPhoneNumber());
        Order savedOrder = entityManager.persist(order);

        entityManager.flush();
        entityManager.clear();

        Optional<Order> retrievedOrder = orderRepository.findByIdAndUserId(savedOrder.getId(), savedUser2.getId());

        assertThat(retrievedOrder).isEmpty();
    }
}
