package com.aiproject.ics.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.internal.UserTypeSqlTypeAdapter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "room_order")
public class RoomOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;
    @OneToMany(mappedBy = "roomOrder", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RoomOrderItem> rooms;
    private double totalPrice;
    private LocalDate checkIn;
    private LocalDate checkOut;
    @Column(nullable = false)
    private String status = "pending";

    public RoomOrder(Users user, List<RoomOrderItem> rooms, double totalPrice, LocalDate checkIn, LocalDate checkOut, String status) {
        this.user = user;
        this.rooms = rooms;
        this.totalPrice = totalPrice;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = "pending";
    }

    public RoomOrder() {

    }
}
