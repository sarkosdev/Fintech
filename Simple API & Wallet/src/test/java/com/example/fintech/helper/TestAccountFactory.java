package com.example.fintech.helper;

import com.example.fintech.entity.Account;
import com.example.fintech.entity.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * Test Account Factory Class
 *
 * This class allows us to Test our Account Entity functionalities when it comes to Unit Testing
 * Since our Entity uses Lombock, and we have specified that no setters are allowed for IDs
 * there was the need to create a workaround, to overcome this design decisions
 */
public class TestAccountFactory {

    // Creates an Account Entity with an ID for Unit Testing
    public static Account accountWithId(Long id) {
        Account account = new Account();
        setId(account, id);
        account.setBalance(new BigDecimal(500));
        return account;
    }

    // Sets the ID on Account Entity for Unit Testing
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
