package com.aiproject.ics.dto;

import java.time.LocalDate;

public class OrderDto {
    private Integer userId;
    private Integer RoomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer nights;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoomId() {
        return RoomId;
    }

    public void setRoomId(Integer roomId) {
        RoomId = roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getNights() {
        return nights;
    }

    public void setNights(Integer nights) {
        this.nights = nights;
    }
}
