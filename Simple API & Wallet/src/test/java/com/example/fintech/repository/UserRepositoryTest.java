package com.example.fintech.repository;


import com.example.fintech.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User Repository Test Class
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * Test Saving User Successfully
     */
    @Test
    void UserRepository_Save_ReturnSavedUser(){

        // ARRANGE
        User user = new User();
        user.setEmail("la@la.com");
        user.setConfirmedAccount(false);
        user.setConfirmationCode("12345678");

        // ACT
        User savedUser = userRepository.save(user);

        // ASSERT
        assertNotNull(savedUser);


    }

}
