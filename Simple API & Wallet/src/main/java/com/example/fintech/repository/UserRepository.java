package com.example.fintech.repository;

import com.example.fintech.dto.ConfirmationCodeDTO;
import com.example.fintech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find User by 'email'
    Optional<User> findByEmail(String email);

    // Query returns 'confirmationCode' by Email
    @Query("SELECT new com.example.fintech.dto.ConfirmationCodeDTO(u.confirmationCode) FROM User u WHERE u.email = :email")
    Optional<ConfirmationCodeDTO> findConfirmationCodeByEmail(String email);
}
