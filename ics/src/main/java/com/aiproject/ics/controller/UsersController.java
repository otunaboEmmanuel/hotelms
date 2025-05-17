package com.aiproject.ics.controller;

import com.aiproject.ics.dto.UserDto;
import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;
import com.aiproject.ics.repository.UsersRepository;
import com.aiproject.ics.service.JwtService;
import com.aiproject.ics.service.UsersService;
import com.aiproject.ics.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> data){
        Map<String,String> response=new HashMap<>();
        Users users=repository.findByEmail(data.get("email")).orElse(null);
        if (users!=null){
            String password1= data.get("password");
            String password2= data.get("repeatPassword");
            if (Objects.equals(password1, password2)){
                users.setPassword(passwordEncoder.encode(password1));
                repository.save(users);
                response.put("code","00");
                response.put("message", "password changed successfully");
            }
            else {
                response.put("code","100");
                response.put("message", "password do not match");
            }
        }else {
            response.put("code","100");
            response.put("message", "email does not exist");
        }
        return ResponseEntity.ok(response);
    }
    @PostMapping("/createAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody Users users){
        Map<String,String> response=new HashMap<>();
        Users users1=repository.findByEmail(users.getEmail()).orElse(null);
        if (users1==null){
            users1=new Users();
            users1.setUserName(users.getUserName());
            users1.setRole(Roles.ADMIN);
            String rawPassword= PasswordGenerator.generatePassword();
            users1.setPassword(passwordEncoder.encode(rawPassword));
            try{
                String htmlContent = "<html><body>" +
                        "<p>Hi " + users.getUserName() + ",</p>" +
                        "<p> Welcome To Acadia Grand Hotel, you have been successfully registered as an admin </p>"+
                        "<p>You have been profiled successfully on our Hotel Management System AI application. </p>" +
                        "<p>Please use your Username and password given below to login </p>" +
                        "<p><b>Password: " + rawPassword + "</b></p>" +
                        "<p> See below for more details </p>" +
                        "</body></html>";
            }catch (Exception e){
                throw new RuntimeException("email could not be sent");
            }
            repository.save(users1);
            response.put("code", "00");
            response.put("message","admin created successfully, check email for credentials ");
        }else{
            response.put("code", "100");
            response.put("message","email already exists");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(@RequestBody Users users, @PathVariable Integer id){
        Map<String,String> response=new HashMap<>();
        Users users1=repository.findById(id).orElse(null);
        if (users1!=null){
            users1.setRole(users.getRole());
            users1.setPassword(passwordEncoder.encode(users.getPassword()));
            users1.setEmail(users.getEmail());
            users1.setUserName(users.getUserName());
            repository.save(users1);
            response.put("code", "00");
            response.put("message","updated successfully");
        }else{
            response.put("code", "100");
            response.put("message","user does not exist");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id){
        Map<String,String> response=new HashMap<>();
        Users users=repository.findById(id).orElse(null);
        if (users!=null){
            repository.deleteById(id);
            response.put("code", "00");
            response.put("message","user deleted successfully");
        }else{
            response.put("code", "00");
            response.put("message","user does not exist");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/allUsers")
    public ResponseEntity<?> allUsers(){
        List<Users> usersList=repository.findAll();
        List<UserDto> dtoList=usersList.stream()
                .map(UserDto::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }
    @GetMapping("/findByRole/{role}")
    public ResponseEntity<?> userRole(@PathVariable String role){
        List<Users> users=repository.findByRole(role);
        List<UserDto> dtoList=users.stream().map(UserDto::new).toList();
        return ResponseEntity.ok(dtoList);
    }


}
