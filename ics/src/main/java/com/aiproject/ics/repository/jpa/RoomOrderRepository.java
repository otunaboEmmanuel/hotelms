package com.aiproject.ics.repository.jpa;

import com.aiproject.ics.entity.RoomOrder;
import com.aiproject.ics.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomOrderRepository extends JpaRepository<RoomOrder, Integer> {
    Optional<RoomOrder> findByUser(Users users);

    List<RoomOrder> findAllByUserAndStatus(Users user, String status);
    List<RoomOrder> findByStatus(String status);
}
