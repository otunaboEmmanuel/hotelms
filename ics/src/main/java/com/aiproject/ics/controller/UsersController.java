package com.aiproject.ics.controller;

import com.aiproject.ics.entity.Users;
import com.aiproject.ics.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hotel")

public class UsersController {
    private final UsersService service;

    public UsersController(UsersService service) {
        this.service = service;
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody Users users){
        Users users1=service.addUser(users);
        Map<String, String> response= new HashMap<>();
        if (users1==null){
            response.put("code","100");
            response.put("message","email already exists");
        }else {
            response.put("code","00");
            response.put("message","check emails for credentials");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
