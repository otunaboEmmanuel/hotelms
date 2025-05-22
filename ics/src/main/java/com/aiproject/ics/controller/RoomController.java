package com.aiproject.ics.controller;

import com.aiproject.ics.dto.RoomDto;
import com.aiproject.ics.entity.Room;
import com.aiproject.ics.enums.Availabililty;
import com.aiproject.ics.repository.jpa.RoomRepository;
import com.aiproject.ics.service.RoomService;
import com.aiproject.ics.service.RoomServiceImp;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final RoomServiceImp serviceImp;


    public RoomController(RoomService roomService, RoomRepository roomRepository, RoomServiceImp serviceImp) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.serviceImp = serviceImp;
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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRoom(@RequestParam("name") String name,
                                        @RequestParam("price") String price,
                                        @RequestParam("roomNumber") String roomNumber,
                                        @RequestParam(value = "attachments",required = false) MultipartFile file,
                                        @PathVariable Integer id) throws IOException {
        Map<String, String> response = new HashMap<>();
        Room room1 = roomRepository.findById(id).orElse(null);
        if (room1 != null) {
            if (file != null) {
            room1.setRoomNumber(Integer.valueOf(roomNumber));
            room1.setName(name);
            room1.setPrice(Integer.valueOf(price));
            room1.setAvailabililty(Availabililty.YES);
            String filepath= serviceImp.saveFileToStorage(file);
                room1.setFilepath(RoomServiceImp.DIRECTORY_PATH+filepath);
                room1.setFileType(file.getContentType());
                room1.setFileName(file.getOriginalFilename());
                System.out.println(room1);
                roomRepository.save(room1);
                response.put("code", "00");
                response.put("message", " Room successfully updated".toUpperCase());
            } else {
                response.put("code", "100");
                response.put("message", "file does not exist".toUpperCase());
            }
        }else {
            response.put("code", "100");
            response.put("message", "room does not exist".toUpperCase());
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Integer id){
        Map<String, String> response =new HashMap<>();
        Room room=roomRepository.findById(id).orElse(null);
        if (room!=null){
            roomRepository.deleteById(id);
            response.put("code", "00");
            response.put("message", "Room successfully deleted".toUpperCase());
        }else {
            response.put("code", "00");
            response.put("message", "Room does not exist".toUpperCase());
        }
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allRooms")
    public ResponseEntity<?> allRooms(){
        List<Room> rooms=roomRepository.findAll();
        List<RoomDto> roomDos=rooms.stream()
                .map(RoomDto::new).toList();
        return ResponseEntity.ok(roomDos);
    }
    @GetMapping("/findAvailableRooms/{available}")
    public ResponseEntity<?> findRoom(@PathVariable String available){
        available= String.valueOf(Availabililty.YES);
        List<Room> rooms =roomRepository.findByAvailabililty(Availabililty.valueOf(available));
        return ResponseEntity.ok(rooms);
    }

}
