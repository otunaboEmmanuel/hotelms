package com.aiproject.ics.dto;

import com.aiproject.ics.entity.Room;
import com.aiproject.ics.enums.Availabililty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class RoomDto {
    private Integer id;
    private Integer price;
    private String name;
    private Integer roomNumber;
    @Enumerated(EnumType.STRING)
    private Availabililty availabililty;
    private String filepath;

    public RoomDto(Room room) {
        this.id = room.getId();
        this.price = room.getPrice();
        this.name = room.getName();
        this.roomNumber = room.getRoomNumber();
        this.availabililty = room.getAvailabililty();
        this.filepath = room.getFilepath();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Availabililty getAvailabililty() {
        return availabililty;
    }

    public void setAvailabililty(Availabililty availabililty) {
        this.availabililty = availabililty;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
