package com.example.fintech.helper;

import com.example.fintech.entity.User;

import java.lang.reflect.Field;

/**
 * Test User Factory Class
 *
 * This class allows us to Test our User Entity functionalities when it comes to Unit Testing
 * Since our Entity uses Lombock, and we have specified that no setters are allowed for IDs
 * there was the need to create a workaround, to overcome this design decisions
 */
public class TestUserFactory {

    // Creates an User Entity with an ID for Unit Testing
    public static User userWithId(Long id) {
        User user = new User();
        setId(user, id);
        user.setEmail("test@test.com");
        return user;
    }

    // Sets the ID on User Entity for Unit Testing
    private static void setId(Object entity, Long id) {
        try {
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
