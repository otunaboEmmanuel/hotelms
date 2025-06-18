package com.aiproject.ics.service;

import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;
import com.aiproject.ics.repository.jpa.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User userDetails = oauthToken.getPrincipal();

        String email = (String) userDetails.getAttributes().get("email");
        String name = (String) userDetails.getAttributes().get("name");
        if (email == null) email = (String) userDetails.getAttributes().get("login"); // fallback for GitHub

        String finalEmail = email;
        Users user = userRepository.findByEmail(email).orElseGet(() -> {
            Users newUser = new Users();
            newUser.setEmail(finalEmail);
            newUser.setUserName(name);
            newUser.setRole(Roles.USER);
            return userRepository.save(newUser);
        });

        String token = jwtService.generateToken(user.getId(), user.getRole(), user.getUsername(), user.getEmail());

//        response.sendRedirect("http://localhost:3000/oauth-success?token=" + token); // redirect to frontend
        response.setContentType("text/html");
        response.getWriter().write("<h2>Login Successful</h2><p>Token: " + token + "</p>");

    }
}
