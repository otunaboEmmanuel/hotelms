package com.aiproject.ics.controller;

import com.aiproject.ics.entity.Room;
import com.aiproject.ics.enums.Availabililty;
import com.aiproject.ics.repository.jpa.RoomRepository;
import com.aiproject.ics.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;
    private final RoomRepository roomRepository;


    public RoomController(RoomService roomService, RoomRepository roomRepository) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> createRoom(@RequestParam("name")String name,
                                      @RequestParam("price") String price,
                                      @RequestParam("roomNumber")String roomNumber,
                                      @RequestParam(value = "attachments",required = false) MultipartFile file,
                                      Room room){
      room.setRoomNumber(Integer.valueOf(roomNumber));
      room.setName(name);
      room.setAvailabililty(Availabililty.YES);
      room.setPrice(Integer.valueOf(price));
      Map<String,String> uploadResponse=roomService.uploadImageToFileSystem(file,room);
      return new ResponseEntity<>(uploadResponse,HttpStatus.OK);
  }
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/downloadRequest/{id}")
    public ResponseEntity<?> download(@PathVariable Integer id) throws IOException {
        Room room = roomRepository.findById(id).orElse(null);

        System.out.println("Fetched room: " + room);
        if (room != null) {
            System.out.println("File name: " + room.getFileName());
            System.out.println("File path: " + room.getFilepath());
            System.out.println("File type: " + room.getFileType());
        }

        Map<String, String> response = new HashMap<>();
        if (room == null || room.getFileName() == null) {
            response.put("code", "100");
            response.put("message", "room does not exist");
            return ResponseEntity.ok(response);
        }

        byte[] imageData = roomService.downloadImageFromFileSystem(room.getFilepath());
        String contentType = room.getFileType();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(contentType))
                .body(imageData);
    }

}
