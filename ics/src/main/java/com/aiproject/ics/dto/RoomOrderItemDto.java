package com.aiproject.ics.dto;

import com.aiproject.ics.entity.RoomOrder;
import com.aiproject.ics.entity.RoomOrderItem;

public class RoomOrderItemDto {

  private String roomName;
  private Integer roomNumber;

    public RoomOrderItemDto() {
    }

    public RoomOrderItemDto(RoomOrderItem roomOrderItem) {
        this.roomName = roomOrderItem.getRoom().getName();
        this.roomNumber = roomOrderItem.getRoom().getRoomNumber();
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }
}
