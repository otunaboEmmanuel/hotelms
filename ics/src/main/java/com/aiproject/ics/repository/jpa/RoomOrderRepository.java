package com.aiproject.ics.repository.jpa;

import com.aiproject.ics.entity.RoomOrder;
import com.aiproject.ics.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomOrderRepository extends JpaRepository<RoomOrder, Integer> {
    Optional<RoomOrder> findByUser(Users users);

        @Query("""
        SELECT r
        FROM RoomOrder r
        WHERE r.user = :user
        AND r.status = :status
    """)
    Page<RoomOrder> findAllByUserAndStatus(Pageable pageable,Users user, String status);
    @Query("""
            SELECT order
            FROM RoomOrder order
            WHERE order.status= :status
            """)
    Page<RoomOrder> findPagesByStatus(Pageable pageable,String status);

    @Query("""
            SELECT order
            FROM RoomOrder order
            """)
    Page<RoomOrder> findAllOrders(Pageable pageable);
}
