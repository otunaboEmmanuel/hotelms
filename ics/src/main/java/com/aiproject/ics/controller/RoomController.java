package com.aiproject.ics.controller;

import com.aiproject.ics.entity.Room;
import com.aiproject.ics.enums.Availabililty;
import com.aiproject.ics.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
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
}
