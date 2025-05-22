package com.aiproject.ics.repository.jpa;

import com.aiproject.ics.entity.Room;
import com.aiproject.ics.enums.Availabililty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Integer> {
    Optional<Room> findByName(String name);


    List<Room> findByAvailabililty(Availabililty availabililty);
}
