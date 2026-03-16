package com.example.fintech.repository;

import com.example.fintech.dto.WalletDTO;
import com.example.fintech.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Account Repository Interface
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Find account by User id
    Optional<Account> findByUser_Id(Long id);

    // Find account by User email
    Optional<Account> findByUser_Email(String email);

    // Get all available wallets on the system besides user its own
    @Query(" SELECT new com.example.fintech.dto.WalletDTO( u.email, a.id) FROM Account a JOIN a.user u WHERE u.email <> :email ORDER BY u.email")
    List<WalletDTO> findAllWallets(String email);
}
