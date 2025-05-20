package com.aiproject.ics.controller;

import com.aiproject.ics.dto.UserDto;
import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;
import com.aiproject.ics.repository.jpa.UsersRepository;
import com.aiproject.ics.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {
    private final UsersRepository repository;

    public StaffController(UsersRepository repository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.repository = repository;
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/usersOnly")
    public ResponseEntity<?> findOnlyUsers(){
        List<Users> findUsers=repository.findByRole(Roles.USER);
        List<UserDto> userDtoList=findUsers.stream()
                .map(UserDto::new)
                .toList();
        return ResponseEntity.ok(userDtoList);
    }
}
