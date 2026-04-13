package com.bookstore.dto.Admin;

import com.bookstore.dto.Book.BookResponse;
import com.bookstore.dto.Category.CategoryResponse;
import com.bookstore.dto.Order.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        Long totalActiveUsers,
        BigDecimal weeklyRevenue,
        Long orderInWeek,
        Long orderToday,
        BookResponse alertBook,
        List<BigDecimal> dailyRevenue,
        List<OrderResponse> recentOrders,
        List<BookResponse> topSellerBooks,
        List<Long> numberOfBooksInWeek,
        List<CategoryResponse> topCategories,
        List<Double> percentOfTopCategories
) {
}
