package com.aiproject.ics.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RoomOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    @JsonBackReference
    private Room room;
    private Integer nights;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "order_id")
    private RoomOrder roomOrder;

    public RoomOrderItem(Room room, Integer nights, RoomOrder roomOrder) {
        this.room = room;
        this.nights = nights;
        this.roomOrder = roomOrder;
    }
}
