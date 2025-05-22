package com.aiproject.ics.dto;

import com.aiproject.ics.entity.RoomOrder;
import com.aiproject.ics.entity.RoomOrderItem;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RoomOrderDto {
    private Integer id;
    private String userName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String status;
    private double totalPrice;
    private List<RoomOrderItemDto> rooms;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RoomOrderDto(RoomOrder roomOrder) {
        this.id=roomOrder.getId();
        this.status=roomOrder.getStatus();
        this.checkIn = roomOrder.getCheckIn();
        this.userName=roomOrder.getUser().getUsername();
        this.checkOut = roomOrder.getCheckOut();
        this.totalPrice = roomOrder.getTotalPrice();
        this.rooms = roomOrder.getRooms().stream()
                .map(RoomOrderItemDto::new).collect(Collectors.toList());
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<RoomOrderItemDto> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomOrderItemDto> room) {
        this.rooms = room;
    }
}
