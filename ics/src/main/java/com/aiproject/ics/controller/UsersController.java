package com.aiproject.ics.controller;

import com.aiproject.ics.dto.UserDto;
import com.aiproject.ics.entity.Users;
import com.aiproject.ics.repository.UsersRepository;
import com.aiproject.ics.service.JwtService;
import com.aiproject.ics.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hotel")

public class UsersController {
    private final UsersService service;
    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsersController(UsersService service, UsersRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.service = service;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
    @PostMapping("/login")
    public ResponseEntity<?>loginUser(@RequestBody Map<String, String> data ){
        Map<String,String> response=new HashMap<>();
        Users users=repository.findByUserName(data.get("userName")).orElse(null);
        if (users!=null){
            String rawPassword= data.get("password");
            String encodedPassword= users.getPassword();
            Boolean matches=passwordEncoder.matches(rawPassword,encodedPassword);
            if (matches){
                response.put("code","00");
                response.put("message", "login successfully");
                response.put("token", jwtService.generateToken(users.getId(),users.getRole(), users.getUsername(), users.getEmail()));
            }else {
                response.put("code","100");
                response.put("message", "passwords do not match");
            }
        }else{
            response.put("code","100");
            response.put("message", "user does not exist");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        List<Users> usersList=repository.findAll();
        List<UserDto> userDtos=usersList.stream().map(
                (UserDto::new)
        ).toList();
        return ResponseEntity.ok(userDtos);
    }
}
