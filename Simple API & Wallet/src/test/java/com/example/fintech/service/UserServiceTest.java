package com.example.fintech.service;

import com.example.fintech.config.kafka.producer.RequestEventProducer;
import com.example.fintech.dto.ConfirmationCodeDTO;
import com.example.fintech.dto.RequestEvent;
import com.example.fintech.entity.User;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * User Service Test Class
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RequestEventProducer requestEventProducer;

    /**
     * Test User Successfully Registration
     */
    @Test
    void UserService_RegisterUserSuccessfully_shouldReturnUser() {

        // Arrange - Mock User
        User user = new User();
        user.setName("Lily");
        user.setEmail("lily@lily.com");
        user.setPassword("12345678");

        // Test email already exists functionality
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        // Test password encoder functionality
        when(passwordEncoder.encode("12345678"))
                .thenReturn("encodedPassword");

        // Mock .save() method and return the User supposed to be saved
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        User result = userService.registerUser(user);

        // ASSERT
        // Check data integrity
        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertFalse(result.isConfirmedAccount());
        assertNotNull(result.getConfirmationCode());

        // Check number of invocations per class
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("12345678");
    }

    /**
     * Test User Registration Business Exception
     */
    @Test
    void UserService_RegisterUser_shouldReturnBusinessException_whenEmailDosentExists(){

        // Mock User -> Arrange
        User user = new User();
        user.setEmail("lily@lily.com");

        // Checks for the Email - it must return a new user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(new User()));

        // Throws the Business Exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.registerUser(user);
        });

        // Check Business Exception message
        assertEquals("Email already taken", exception.getMessage());

        // Check that was never saved
        verify(userRepository, never()).save(any());
    }


    /**
     * Test User Confirmation Successfully
     */
    @Test
    void  UserService_UserConfirmationSuccessfully(){

        // Arrange - Mock User
        User user = new User();
        user.setName("Lily");
        user.setEmail("lily@lily.com");
        user.setPassword("12345678");
        user.setConfirmationCode("123456");
        user.setConfirmedAccount(false);

        // If user exists return that exact same user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Act - Invoke method
        userService.confirmUser("lily@lily.com", "123456");

        // Sends Kafka message aswell
        verify(requestEventProducer).send(any(RequestEvent.class));

        // Assert - Check that the values are actually correct
        assertTrue(user.isConfirmedAccount());
        // Check that user was actually saved at the end
        verify(userRepository).save(user);
    }

    /**
     * Test User Confirmation Code Illegal Exception - User was not found
     */
    @Test
    void  UserService_UserConfirmation_shouldReturnIllegalException_whenUserWasNotFound() {

        // Checks for the Email - it must return no user
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        // Throws the IllegalStateException Exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.confirmUser("lil2@lily.com", "123456");
        });

        // Check IllegalStateException message
        assertEquals("User not found", exception.getMessage());

        // Check that was never saved
        verify(userRepository, never()).save(any());
    }

    /**
     * Test User Confirmation Code Business Exception - Account Already Confirmed
     */
    @Test
    void UserService_UserConfirmation_shouldReturnBusinessException_whenAccountAllreadyConfirmed() {

        User user = new User();
        user.setEmail("lily@lily.com");
        user.setConfirmationCode("123456");
        user.setConfirmedAccount(true);

        // Checks for the Email - Its must return same user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Throws the Business Exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.confirmUser("lily@lily.com", "123456");
        });

        // Check BusinessException message
        assertEquals("This account is already confirmed", exception.getMessage());

        // Check that was never saved
        verify(userRepository, never()).save(any());
    }

    /**
     * Test User Confirmation Code Business Exception - Invalid Confirmation Code
     */
    @Test
    void UserService_UserConfirmation_shouldReturnBusinessException_whenConfirmationCodeMatches() {

        User user = new User();
        user.setEmail("lily@lily.com");
        user.setConfirmationCode("123456");
        user.setConfirmedAccount(false);

        // Checks for the Email - It must return same user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Throws the Business Exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.confirmUser("lily@lily.com", "wrong");
        });

        // Check BusinessException message
        assertEquals("Invalid confirmation code", exception.getMessage());

        // Check that was never saved
        verify(userRepository, never()).save(any());
    }

    /**
     * Test Check User Confirmation Code Successfully
     */
    @Test
    void UserService_CheckUserCode_shouldReturnDTO_whenUserExists() {

        // ARRANGE
        String email = "lily@lily.com";
        ConfirmationCodeDTO dto = new ConfirmationCodeDTO("123456");

        when(userRepository.findConfirmationCodeByEmail(email))
                .thenReturn(Optional.of(new ConfirmationCodeDTO("123456")));

        // ACT
        ConfirmationCodeDTO result = userService.checkUserCode(email);

        // ASSERT
        assertNotNull(result);
        assertEquals(dto.getUserCode(), result.getUserCode());
    }

    /**
     * Test Check User Confirmation Code Business Exception - User not found
     */
    @Test
    void UserService_CheckUserCode_shouldThrowBusinessException_whenUserNotFound(){

        // ARRANGE
        String email = "lily@lily.com";

        // ACT
        when(userRepository.findConfirmationCodeByEmail(email))
                .thenReturn(Optional.empty());

        // Throws the Business Exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.checkUserCode(email);
        });

        // ASSERT
        // Check BusinessException message
        assertEquals("User not found", exception.getMessage());

        // Check that was never saved
        verify(userRepository, never()).save(any());
    }
}
