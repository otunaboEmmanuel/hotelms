package com.aiproject.ics.service;

import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;
import com.aiproject.ics.repository.jpa.UsersRepository;
import com.aiproject.ics.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImp implements UsersService{
    @Autowired
    private UsersRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public Users addUser(Users users) {
        if (users == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Users users1 = repository.findByUserName(users.getUsername()).orElse(null);
        if (users1 == null) {
            users1=new Users();
            users1.setUserName(users.getUsername());
            users1.setEmail(users.getEmail());
            String rawPassword = PasswordGenerator.generatePassword(); // Corrected this line
            String encodedPassword = passwordEncoder.encode(rawPassword);
            users1.setPassword(encodedPassword);
            users1.setRole(Roles.USER);
            try {
                String htmlContent = "<html><body>" +
                        "<p>Hi " + users.getUsername() + ",</p>" +
                        "<p> Welcome To Acadia Grand Hotel, we are happy that you have registered for our application</p>"+
                        "<p>You have been profiled successfully on our Hotel Management System AI application. </p>" +
                        "<p>Please use your Username and password given below to login </p>" +
                        "<p><b>Password: " + rawPassword + "</b></p>" +
                        "<p> See below for more details </p>" +
                        "</body></html>";

                MailBody mailBody = new MailBody(users.getEmail(), "Registration info", "This is your info " + htmlContent);
                emailService.sendSimpleMessage(mailBody);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
             return repository.save(users1); // Save the updated user
        } else {
            return null;
        }
    }

}
