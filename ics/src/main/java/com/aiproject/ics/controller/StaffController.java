package com.aiproject.ics.controller;

import com.aiproject.ics.dto.UserDto;
import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;
import com.aiproject.ics.repository.jpa.UsersRepository;
import com.aiproject.ics.service.EmailService;
import com.aiproject.ics.service.MailBody;
import com.aiproject.ics.utils.PasswordGenerator;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/staff")
public class StaffController {
    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public StaffController(UsersRepository repository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createStaff(@RequestBody Users users) {
        Map<String, String> response = new HashMap<>();
        Users users1 = repository.findByEmail(users.getEmail()).orElse(null);
        if (users1 == null) {
            users1 = new Users();
            users1.setUserName(users.getUserName());
            users1.setRole(Roles.STAFF);
            users1.setEmail(users.getEmail());
            String password = PasswordGenerator.generatePassword();
            users1.setPassword(passwordEncoder.encode(password));
            try {
                String htmlContent = "<html><body>" +
                        "<p>Hi " + users.getUserName() + ",</p>" +
                        "<p> Welcome To Acadia Grand Hotel, you have been successfully registered as a staff </p>" +
                        "<p>You have been profiled successfully on our Hotel Management System AI application. </p>" +
                        "<p>Please use your Username and password given below to login </p>" +
                        "<p><b>Password: " + password + "</b></p>" +
                        "<p> See below for more details </p>" +
                        "</body></html>";
                MailBody mailBody = new MailBody(users.getEmail(), "Registration info", "This is your info " + htmlContent);
                emailService.sendSimpleMessage(mailBody);
            } catch (Exception e) {
                throw new RuntimeException("could not send email");
            }
            repository.save(users1);
            response.put("code", "00");
            response.put("message", "admin created successfully, check email for credentials ");
        } else {
            response.put("code", "100");
            response.put("message", "email already exists");
        }
        return ResponseEntity.ok(response);
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
