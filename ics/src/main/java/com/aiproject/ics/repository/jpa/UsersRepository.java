package com.aiproject.ics.repository.jpa;

import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUserName(String userName);

    Optional<Users> findByEmail(String email);
    @Query("""
            SELECT user
            FROM Users user
            """)
    Page<Users> findAllUsers(Pageable pageable);

    @Query("""
            SELECT user
            FROM Users user
            WHERE user.role= :role
            """)
    Page<Users> findByRole(Pageable pageable,Roles role);
}
