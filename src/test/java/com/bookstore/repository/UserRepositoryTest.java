package com.bookstore.repository;

import com.bookstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private void createAndSaveTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123");
        user.setFullName("Test User");
        user.setPhoneNumber("0123456789");
        entityManager.persistFlushFind(user);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUsernameExists() {
        createAndSaveTestUser("user", "user@gmail.com");

        Optional<User> retrievedUser = userRepository.findByUsername("user");

        assertThat(retrievedUser)
                .isPresent()
                .map(User::getUsername)
                .contains("user");
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUsernameDoesNotExist() {
        Optional<User> retrievedUser = userRepository.findByUsername("hacker");

        assertThat(retrievedUser).isEmpty();
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        createAndSaveTestUser("user", "user@gmail.com");

        Optional<User> retrievedUser = userRepository.findByEmail("user@gmail.com");

        assertThat(retrievedUser)
                .isPresent()
                .map(User::getEmail)
                .contains("user@gmail.com");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        Optional<User> retrievedUser = userRepository.findByEmail("user@gmail.com");

        assertThat(retrievedUser).isEmpty();
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        createAndSaveTestUser("user", "user@gmail.com");

        boolean exists = userRepository.existsByUsername("user");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        boolean exists = userRepository.existsByUsername("hacker");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        createAndSaveTestUser("user", "user@asgard.com");

        boolean exists = userRepository.existsByEmail("user@asgard.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("user@asgard.com");

        assertThat(exists).isFalse();
    }
}
