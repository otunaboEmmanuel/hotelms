package com.aiproject.ics.dto;

import java.time.LocalDate;
import java.util.List;

public class OrderDto {
    private Integer userId;
    private List<Integer> RoomIds;

    public List<Integer> getRoomIds() {
        return RoomIds;
    }

    public void setRoomId(List<Integer> roomIds) {
        RoomIds = roomIds;
    }

    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer nights;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
