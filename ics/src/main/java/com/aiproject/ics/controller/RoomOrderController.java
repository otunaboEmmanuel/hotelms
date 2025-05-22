package com.aiproject.ics.controller;

import com.aiproject.ics.dto.OrderDto;
import com.aiproject.ics.entity.Room;
import com.aiproject.ics.entity.RoomOrder;
import com.aiproject.ics.entity.RoomOrderItem;
import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Availabililty;
import com.aiproject.ics.repository.jpa.RoomOrderItemRepository;
import com.aiproject.ics.repository.jpa.RoomOrderRepository;
import com.aiproject.ics.repository.jpa.RoomRepository;
import com.aiproject.ics.repository.jpa.UsersRepository;
import com.aiproject.ics.service.RoomOrderService;
import com.aiproject.ics.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class RoomOrderController {
    private final UsersRepository usersRepository;
    private final RoomRepository roomRepository;
    private final RoomOrderRepository roomOrderRepository;
    private final RoomOrderItemRepository roomOrderItemRepository;

    public RoomOrderController(UsersRepository usersRepository, RoomRepository roomRepository, RoomOrderRepository roomOrderRepository, RoomOrderItemRepository roomOrderItemRepository) {
        this.usersRepository = usersRepository;
        this.roomRepository = roomRepository;
        this.roomOrderRepository = roomOrderRepository;
        this.roomOrderItemRepository = roomOrderItemRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/room")
    public ResponseEntity<?> bookRoom(@RequestBody OrderDto request) {
        Map<String,String> response=new HashMap<>();
        Users user=usersRepository.findById(request.getUserId()).orElse(null);
        Room room=roomRepository.findById(request.getRoomId()).orElse(null);
        if (user==null||room==null){
            response.put("code","100");
            response.put("message","room or user does not exist".toUpperCase());
            return ResponseEntity.badRequest().body(response);
        }
        if (room.getAvailabililty()== Availabililty.NO){
            response.put("code","100");
            response.put("message","room is already booked".toUpperCase());
            return ResponseEntity.badRequest().body(response);
        }
        double totalPrice= room.getPrice()*request.getNights();
       RoomOrder roomOrder=new RoomOrder();
        roomOrder.setUser(user);
        roomOrder.setStatus("pending");
        roomOrder.setCheckIn(request.getCheckIn());
        roomOrder.setCheckOut(request.getCheckOut());
        roomOrder.setTotalPrice(totalPrice);
        roomOrder.setRooms(new ArrayList<>());
        roomOrderRepository.save(roomOrder);

        RoomOrderItem roomOrderItem=new RoomOrderItem(room, request.getNights(), roomOrder);
        roomOrderItemRepository.save(roomOrderItem);

        roomOrder.setRooms(new ArrayList<>(List.of(roomOrderItem)));
        roomOrderRepository.save(roomOrder);
        room.setAvailabililty(Availabililty.NO);
        roomRepository.save(room);

        response.put("code","00");
        response.put("message","Room successfully Booked".toUpperCase());
        return ResponseEntity.ok(response);
    }

}
