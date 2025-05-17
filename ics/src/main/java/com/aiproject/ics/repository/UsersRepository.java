package com.aiproject.ics.repository;

import com.aiproject.ics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUserName(String userName);

    Optional<Users> findByEmail(String email);

    List<Users> findByRole(String role);
}
