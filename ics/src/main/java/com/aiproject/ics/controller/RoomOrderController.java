package com.aiproject.ics.controller;
import com.aiproject.ics.dto.OrderDto;
import com.aiproject.ics.dto.RoomOrderDto;
import com.aiproject.ics.entity.Room;
import com.aiproject.ics.entity.RoomOrder;
import com.aiproject.ics.entity.RoomOrderItem;
import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Availabililty;
import com.aiproject.ics.repository.jpa.RoomOrderItemRepository;
import com.aiproject.ics.repository.jpa.RoomOrderRepository;
import com.aiproject.ics.repository.jpa.RoomRepository;
import com.aiproject.ics.repository.jpa.UsersRepository;
import com.aiproject.ics.service.EmailService;
import com.aiproject.ics.service.MailBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class RoomOrderController {
    private final UsersRepository usersRepository;
    private final RoomRepository roomRepository;
    private final RoomOrderRepository roomOrderRepository;
    private final RoomOrderItemRepository roomOrderItemRepository;
    private final EmailService emailService;

    public RoomOrderController(UsersRepository usersRepository, RoomRepository roomRepository, RoomOrderRepository roomOrderRepository, RoomOrderItemRepository roomOrderItemRepository, EmailService emailService) {
        this.usersRepository = usersRepository;
        this.roomRepository = roomRepository;
        this.roomOrderRepository = roomOrderRepository;
        this.roomOrderItemRepository = roomOrderItemRepository;
        this.emailService = emailService;
    }
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/room")
    public ResponseEntity<?> bookRooms(@RequestBody OrderDto request) {
        Map<String, String> response = new HashMap<>();

        Users user = usersRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            response.put("code", "100");
            response.put("message", "User does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        List<Room> rooms = roomRepository.findAllById(request.getRoomIds()); // expects List<Integer>
        if (rooms.size() != request.getRoomIds().size()) {
            response.put("code", "100");
            response.put("message", "One or more rooms do not exist");
            return ResponseEntity.badRequest().body(response);
        }

        for (Room room : rooms) {
            if (room.getAvailabililty() == Availabililty.NO) {
                response.put("code", "100");
                response.put("message", "One or more rooms are already booked");
                return ResponseEntity.badRequest().body(response);
            }
        }

        double totalPrice = rooms.stream()
                .mapToDouble(room -> room.getPrice() * request.getNights())
                .sum();

        RoomOrder roomOrder = new RoomOrder();
        roomOrder.setUser(user);
        roomOrder.setStatus("pending");
        roomOrder.setCheckIn(request.getCheckIn());
        roomOrder.setCheckOut(request.getCheckOut());
        roomOrder.setTotalPrice(totalPrice);
        roomOrder.setRooms(new ArrayList<>());
        roomOrderRepository.save(roomOrder);

        List<RoomOrderItem> orderItems = new ArrayList<>();
        for (Room room : rooms) {
            RoomOrderItem item = new RoomOrderItem(room, request.getNights(), roomOrder);
            orderItems.add(item);
            room.setAvailabililty(Availabililty.NO); // Mark as booked
            roomRepository.save(room);
        }
        roomOrderItemRepository.saveAll(orderItems);
        roomOrder.setRooms(orderItems);
        RoomOrder savedOrder = roomOrderRepository.save(roomOrder);

        // Email Notification
        try {
            String htmlContent = "<html><body>" +
                    "<p>Hi " + user.getUsername() + ",</p>" +
                    "<p>Welcome to Acadia Grand Hotel!</p>" +
                    "<p>You have successfully booked " + rooms.size() + " room(s). Please wait for admin approval.</p>" +
                    "</body></html>";

            MailBody mailBody = new MailBody(user.getEmail(), "Booking Info", htmlContent);
            emailService.sendSimpleMessage(mailBody);
        } catch (Exception e) {
            System.out.println("Email Error: " + e.getMessage());
        }

        response.put("code", "00");
        response.put("message", "Rooms successfully booked");
        response.put("orderId", String.valueOf(savedOrder.getId()));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN','STAFF')")
    @PostMapping("/cancel-order/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id ){
        Map<String, String> response=new HashMap<>();
        RoomOrder roomOrder=roomOrderRepository.findById(id).orElse(null);
        if (roomOrder!=null){
            roomOrderRepository.deleteById(id);
            response.put("code","00");
            response.put("message","Room order with id "+roomOrder.getId()+ "has been deleted".toUpperCase());
            roomOrder.getRooms().stream().forEach(
                    roomOrderItem -> {
                        Room room=roomOrderItem.getRoom();
                        room.setAvailabililty(Availabililty.YES);
                        roomRepository.save(room);
                    }
            );
        }else{
            response.put("code","100");
            response.put("message", "Order does not exist");
        }
           return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping("/approve-booking/{id}/{status}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @PathVariable String status){
        Map<String,String> response=new HashMap<>();
        RoomOrder order=roomOrderRepository.findById(id).orElse(null);
        if (order != null) {
           if (status.equalsIgnoreCase("approved")||status.equalsIgnoreCase("denied")){
               response.put("code","00");
               response.put("message","successfully updated".toUpperCase());
               order.setStatus(status.toLowerCase());
               roomOrderRepository.save(order);
               if (status.equals("approved")){
               try{
                   String roomNumbers = order.getRooms().stream()
                           .map(item -> String.valueOf(item.getRoom().getRoomNumber()))
                           .collect(Collectors.joining(", "));

                   String htmlContent = "<html><body>" +
                           "<p>Hi " + order.getUser().getUsername() + ",</p>" +
                           "<p>Welcome to Acadia Grand Hotel, we are happy that you have booked a room.</p>" +
                           "<p>Your booking has been <b>approved</b>. Please show this confirmation when you arrive.</p>" +
                           "<p><strong>Order ID:</strong> " + order.getId() + "<br>" +
                           "<strong>Room Number(s):</strong> " + roomNumbers + "</p>" +
                           "</body></html>";

                   MailBody mailBody = new MailBody(
                           order.getUser().getEmail(),
                           "Booking Confirmation",
                           htmlContent
                   );
                   emailService.sendSimpleMessage(mailBody);

               } catch (Exception e) {
                   System.out.println(e.getMessage());
               }
               }else{
                   order.getRooms().forEach(roomOrderItem -> {
                       Room room=roomOrderItem.getRoom();
                       room.setAvailabililty(Availabililty.YES);
                       roomRepository.save(room);
                   }
                   );
                   response.put("code","100");
                   response.put("message","Order has been denied".toUpperCase());
               }

           }else {
               response.put("code","100");
               response.put("message","status must be approved or denied".toUpperCase());
           }
        }else {
            response.put("code","100");
            response.put("message","Room order with"+id+" can not be found".toUpperCase());
        }
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/orders")
    public Page<?> allOrders(@RequestParam(name = "page",defaultValue = "0",required = false)int page,
                             @RequestParam(name = "size",defaultValue = "10",required = false) int size){
        Pageable pageable= PageRequest.of(page,size, Sort.by("id").descending());
        Page<RoomOrder> orders=roomOrderRepository.findAllOrders(pageable);
        return orders.map(RoomOrderDto::new);
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/orders/{status}")
    public ResponseEntity<?> allApprovedOrders(@PathVariable String status,
                                               @RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                               @RequestParam(name = "size",defaultValue = "10",required = false) int size){
        Map<String, String> response=new HashMap<>();
        Pageable pageable= PageRequest.of(page,size, Sort.by("id").descending());
        if (status.equals("approved")){
        Page <RoomOrder> orders=roomOrderRepository.findPagesByStatus(pageable,status);
        List<RoomOrderDto> roomOrderDtos=orders.stream()
                .map(RoomOrderDto::new).toList();
        return ResponseEntity.ok(roomOrderDtos);
        }else {
            response.put("code","100");
            response.put("message","not an approved order".toUpperCase());
            return ResponseEntity.ok(response);
        }
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN','STAFF')")
    @PostMapping("/{userId}")
    public ResponseEntity<?> userOrders(@PathVariable Integer userId,
                                        @RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                        @RequestParam(name = "size",defaultValue = "10",required = false) int size){
        Pageable pageable= PageRequest.of(page,size, Sort.by("id").descending());
        Map<String,String> response=new HashMap<>();
        Users user=usersRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("code", "100");
            response.put("message", "User not found".toUpperCase());
            return ResponseEntity.badRequest().body(response);
        }
        Page<RoomOrder> orders=roomOrderRepository.findAllByUserAndStatus(pageable,user,"approved");
        if (!orders.isEmpty()){
            Page<RoomOrderDto> orderDtos=orders.map(RoomOrderDto::new);
            return ResponseEntity.ok(orderDtos);
        }else{
            response.put("code","100");
            response.put("message","No room has been approved for this user".toUpperCase());
        }
        return ResponseEntity.ok(response);
    }


}



