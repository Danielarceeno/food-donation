package com.example.donation.repository;

import com.example.donation.entity.User;
import com.example.donation.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByTipo(UserType tipo);
}
