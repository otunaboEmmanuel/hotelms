package com.aiproject.ics.controller;

import com.aiproject.ics.dto.LoginDto;
import com.aiproject.ics.dto.UserDto;
import com.aiproject.ics.entity.Otp;
import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;
import com.aiproject.ics.repository.jpa.ForgotPasswordRepository;
import com.aiproject.ics.repository.jpa.UsersRepository;
import com.aiproject.ics.service.EmailService;
import com.aiproject.ics.service.JwtService;
import com.aiproject.ics.service.MailBody;
import com.aiproject.ics.service.UsersService;
import com.aiproject.ics.utils.PasswordGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/hotel")

public class UsersController {
    private final UsersService service;
    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ForgotPasswordRepository userOtpRepository;
    private final EmailService emailService;

    public UsersController(UsersService service, UsersRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, ForgotPasswordRepository userOtpRepository, EmailService emailService) {
        this.service = service;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userOtpRepository = userOtpRepository;
        this.emailService = emailService;
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
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String,String> data){
        Map<String, String> response= new HashMap<>();
      Users user=repository.findByEmail(data.get("email")).orElse(null);
      if (user!=null){
          Integer otp=PasswordGenerator.random();
          Otp otp1=new Otp();
          otp1.setUser(user);
          otp1.setOtp(otp);
          otp1.setExpirationTime(new Date(System.currentTimeMillis()+1000*60*2));
          userOtpRepository.save(otp1);
          emailService.sendSimpleMessage(new MailBody(data.get("email"),
                  "OTP VERIFICATION CODE",
                  "USE THIS OTP TO CHANGE YOUR PASSWORD"+otp));
          response.put("code","00");
          response.put("message","OTP HAS BEEN SENT FOR VERIFICATION, CHECK YOUR EMAIL");
      }else {
          response.put("code","100");
          response.put("message", "email does not exist");
      }
      return ResponseEntity.ok(response);
    }
    @PostMapping("/otp-verification")
    public ResponseEntity<?> otpVerification(@RequestBody Map<String,String> data){
        Map<String,String> response=new HashMap<>();
        Users user=repository.findByEmail(data.get("email")).orElse(null);
        Otp otp= userOtpRepository.findByOtpAndUser(Integer.valueOf(data.get("otp")),user).orElse(null);
        if(user!=null&&otp!=null){
           if (!otp.getExpirationTime().before(new Date())){
               response.put("code","00");
               response.put("message","OTP HAS BEEN VERIFIED");
           }else{
               userOtpRepository.deleteById(otp.getId());
               response.put("code","100");
               response.put("message","OTP HAS EXPIRED");
           }
        }else{
            response.put("code","100");
            response.put("message", "email does not exist");
        }
        return ResponseEntity.ok(response);
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody Users users){
        Map<String,String> response=new HashMap<>();
        Users users1=repository.findByEmail(users.getEmail()).orElse(null);
        if (users1==null){
            users1=new Users();
            users1.setUserName(users.getUsername());
            users1.setRole(Roles.ADMIN);
            String rawPassword= PasswordGenerator.generatePassword();
            users1.setPassword(passwordEncoder.encode(rawPassword));
            try{
                String htmlContent = "<html><body>" +
                        "<p>Hi " + users.getUsername() + ",</p>" +
                        "<p> Welcome To Acadia Grand Hotel, you have been successfully registered as an admin </p>"+
                        "<p>You have been profiled successfully on our Hotel Management System AI application. </p>" +
                        "<p>Please use your Username and password given below to login </p>" +
                        "<p><b>Password: " + rawPassword + "</b></p>" +
                        "<p> See below for more details </p>" +
                        "</body></html>";
                MailBody mailBody = new MailBody(users.getEmail(), "Registration info", "This is your info " + htmlContent);
                emailService.sendSimpleMessage(mailBody);
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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, String> users, @PathVariable Integer id){
        Map<String,String> response=new HashMap<>();
        Users users1=repository.findById(id).orElse(null);
        if (users1!=null){
            users1.setRole(Roles.valueOf(users.get("role")));
            users1.setPassword(passwordEncoder.encode(users.get("password")));
            users1.setEmail(users.get("email"));
            users1.setUserName(users.get("userName"));
            repository.save(users1);
            response.put("code", "00");
            response.put("message","updated successfully");
        }else{
            response.put("code", "100");
            response.put("message","user does not exist");
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allUsers")
    public ResponseEntity<?> allUsers(){
        List<Users> usersList=repository.findAll();
        List<UserDto> dtoList=usersList.stream()
                .map(UserDto::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/findByRole/{role}")
    public ResponseEntity<?> userRole(@PathVariable String role){
        String rolecase=role.toUpperCase();
        List<Users> users=repository.findByRole(Roles.valueOf(rolecase));
        List<UserDto> dtoList=users.stream().map(UserDto::new).toList();
        return ResponseEntity.ok(dtoList);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateRole/{id}")
    public ResponseEntity<?> updateRole(@RequestBody Map<String,String> data,@PathVariable Integer id){
        Map<String,String> response=new HashMap<>();
        Users users=repository.findById(id).orElse(null);
        if (users!=null){
            users.setRole(Roles.valueOf(data.get("role")));
            repository.save(users);
            response.put("code", "00");
            response.put("message","updated successfully");
        }else{
            response.put("code", "100");
            response.put("message","user does not exist");
        }
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createStaff(@RequestBody Users users) {
        System.out.println("received request with email = " + users.getEmail());
        System.out.println("username = " + users.getUsername());

        System.out.println("recieved request with "+users.getUsername());
        Map<String, String> response = new HashMap<>();
        Users users1 = repository.findByEmail(users.getEmail()).orElse(null);
        if (users.getEmail() == null || users.getEmail().isEmpty()) {
            response.put("code", "101");
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (users1 == null) {
            users1 = new Users();
            users1.setUserName(users.getUsername());
            users1.setRole(Roles.STAFF);
            users1.setEmail(users.getEmail());
            String password = PasswordGenerator.generatePassword();
            users1.setPassword(passwordEncoder.encode(password));
            repository.save(users1);
            response.put("code", "00");
            response.put("message", "admin created successfully, check email for credentials ");
            try {
                String htmlContent = "<html><body>" +
                        "<p>Hi " + users.getUsername() + ",</p>" +
                        "<p> Welcome To Acadia Grand Hotel, you have been successfully registered as a staff </p>" +
                        "<p>You have been profiled successfully on our Hotel Management System AI application. </p>" +
                        "<p>Please use your Username and password given below to login </p>" +
                        "<p><b>Password: " + password + "</b></p>" +
                        "<p> See below for more details </p>" +
                        "</body></html>";
                MailBody mailBody = new MailBody(users.getEmail(), "Registration info", "This is your info " + htmlContent);
                emailService.sendSimpleMessage(mailBody);
            } catch (Exception e) {
                e.printStackTrace(); // Or log the error
                throw new RuntimeException("could not send email", e);
            }
        } else {
            response.put("code", "100");
            response.put("message", "email already exists");
        }
        return ResponseEntity.ok(response);
    }

}
