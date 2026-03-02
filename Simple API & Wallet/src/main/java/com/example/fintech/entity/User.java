package com.example.fintech.entity;

import com.example.fintech.service.UserService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * This class maps the DB Entity 'User'
 */

@Data
@Entity
@Table( name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)                                       // Block accesses to 'id' setter
    private Long id;

    @NotBlank( message = "Field 'name' cannot be blank")
    @Column( name = "name", nullable = false)                       // Define table name and it cannot be null in DB
    private String name;

    @Email
    @NotBlank( message = "Field 'email' cannot be blank")
    @Column( name = "email", nullable = false)
    private String email;

    @Size( min = 8, message = "Field 'password' must be at least 8 characters long")
    @Column( name = "password", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "confirmation_code", nullable = false)
    private String confirmationCode;

    @Column(name = "confirmed_account", nullable = false)
    private boolean confirmedAccount;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;


}
