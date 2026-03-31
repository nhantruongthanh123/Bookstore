package com.bookstore.service.cart;

import com.bookstore.dto.Cart.CartItemResponse;
import com.bookstore.dto.Cart.CartResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(){
        Long userId = AuthenticationUtil.getCurrentUserId();

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);

        if (optionalCart.isEmpty()) {
            return new CartResponse(null, userId, List.of(), BigDecimal.ZERO);
        }

        Cart cart = optionalCart.get();

        List<CartItemResponse> itemResponses = cart.getItems().stream().map(item -> {
            Book book = item.getBook();

            BigDecimal quantityBD = BigDecimal.valueOf(item.getQuantity());
            BigDecimal subTotal = book.getPrice().multiply(quantityBD);

            return new CartItemResponse(
                    item.getId(),
                    book.getId(),
                    book.getTitle(),
                    book.getCoverImage(),
                    book.getPrice(),
                    item.getQuantity(),
                    subTotal
            );
        }).toList();

        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), userId, itemResponses, totalPrice);
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long bookId, Integer quantity) {
        Long userId = AuthenticationUtil.getCurrentUserId();

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(quantity);

            cart.getItems().add(newItem);
        }

        return getCart();
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long cartItemId, Integer quantity){
        Long userId = AuthenticationUtil.getCurrentUserId();

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this cart item");
        }

        Book book = item.getBook();
        if (quantity > book.getQuantity()) {
            throw new RuntimeException("Don't have enough stock");
        }

        item.setQuantity(quantity);
        return getCart();
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long cartItemId){
        Long userId = AuthenticationUtil.getCurrentUserId();

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to remove this cart item");
        }

        cartItemRepository.delete(item);
        return getCart();
    }
}
