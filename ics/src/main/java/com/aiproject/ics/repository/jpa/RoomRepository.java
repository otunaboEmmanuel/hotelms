package com.aiproject.ics.repository.jpa;

import com.aiproject.ics.entity.Room;
import com.aiproject.ics.enums.Availabililty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Integer> {
    Optional<Room> findByName(String name);

    @Query("""
            SELECT room
            FROM Room room
            """)
    Page<Room> findAllRoom(Pageable pageable);
    List<Room> findByAvailabililty(Availabililty availabililty);
}
