package com.bookstore.service.dashboard;

import com.bookstore.dto.Admin.DashboardResponse;
import com.bookstore.dto.Book.BookResponse;
import com.bookstore.dto.Category.CategoryResponse;
import com.bookstore.dto.Order.OrderResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.OrderStatus;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService
{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderMapper orderMapper;
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = LocalDateTime.now();

        long totalActiveUsers = userRepository.countByEnabledTrue();
        List<Order> ordersInWeek = orderRepository.findByOrderDateBetweenOrderByOrderDateAsc(startDateTime, endDateTime);

        Map<LocalDate, BigDecimal> dailyRevenueMap = new LinkedHashMap<>();
        Map<LocalDate, Long> dailyBookCountMap = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            dailyRevenueMap.put(date, BigDecimal.ZERO);
            dailyBookCountMap.put(date, 0L);
        }

        BigDecimal weeklyRevenue = BigDecimal.ZERO;
        long orderInWeek = 0L;
        long orderToday = 0L;

        for (Order order : ordersInWeek) {
            if (order.getOrderDate() == null || order.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }
            LocalDate orderDate = order.getOrderDate().toLocalDate();
            if (!dailyRevenueMap.containsKey(orderDate)) {
                continue;
            }

            BigDecimal amount = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
            dailyRevenueMap.put(orderDate, dailyRevenueMap.get(orderDate).add(amount));
            weeklyRevenue = weeklyRevenue.add(amount);
            orderInWeek++;

            long bookCountInOrder = order.getOrderItems().stream()
                    .map(OrderItem::getQuantity)
                    .filter(Objects::nonNull)
                    .mapToLong(Integer::longValue)
                    .sum();
            dailyBookCountMap.put(orderDate, dailyBookCountMap.get(orderDate) + bookCountInOrder);

            if (orderDate.equals(today)) {
                orderToday++;
            }
        }

        List<BigDecimal> dailyRevenue = dailyRevenueMap.values().stream()
                .map(value -> value.setScale(2, RoundingMode.HALF_UP))
                .toList();
        List<Long> numberOfBooksInWeek = dailyBookCountMap.values().stream().toList();
        weeklyRevenue = weeklyRevenue.setScale(2, RoundingMode.HALF_UP);

        BookResponse alertBook = bookRepository.findBooksWithLowestQuantity(PageRequest.of(0, 1)).stream()
                .findFirst()
                .map(bookMapper::toResponse)
                .orElse(null);

        List<OrderResponse> recentOrders = orderRepository.findTop3ByOrderByOrderDateDesc().stream()
                .map(orderMapper::toOrderResponse)
                .toList();

        List<Long> topSellerBookIds = orderRepository.findTopSellerBookIds(PageRequest.of(0, 3));
        Map<Long, Book> bookMapById = new HashMap<>();
        if (!topSellerBookIds.isEmpty()) {
            for (Book book : bookRepository.findByIdIn(topSellerBookIds)) {
                bookMapById.put(book.getId(), book);
            }
        }
        List<BookResponse> topSellerBooks = topSellerBookIds.stream()
                .map(bookMapById::get)
                .filter(Objects::nonNull)
                .map(bookMapper::toResponse)
                .toList();

        List<Object[]> topCategoryRows = bookRepository.findTopCategoriesByBookCount(PageRequest.of(0, 3));
        List<CategoryResponse> topCategories = topCategoryRows.stream()
                .map(row -> (Category) row[0])
                .map(categoryMapper::toResponse)
                .toList();

        long totalBooks = bookRepository.countAvailableBooks();
        List<Double> percentOfTopCategories = topCategoryRows.stream()
                .map(row -> ((Number) row[1]).longValue())
                .map(count -> totalBooks == 0
                        ? 0.0
                        : BigDecimal.valueOf((double) count * 100 / totalBooks)
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue())
                .toList();

        return new DashboardResponse(
                totalActiveUsers,
                weeklyRevenue,
                orderInWeek,
                orderToday,
                alertBook,
                dailyRevenue,
                recentOrders,
                topSellerBooks,
                numberOfBooksInWeek,
                topCategories,
                percentOfTopCategories
        );
    }
}
