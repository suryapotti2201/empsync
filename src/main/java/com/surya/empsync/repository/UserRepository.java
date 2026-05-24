package com.surya.empsync.repository;

import com.surya.empsync.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

    boolean existsByUserName(@NotBlank @Size(min = 3, max = 20) String userName);

    boolean existsByEmailId(@NotBlank @Size(min = 3, max = 50) @Email String emailId);

}
