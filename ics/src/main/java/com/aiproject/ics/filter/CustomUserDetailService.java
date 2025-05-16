package com.aiproject.ics.filter;

import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;
import com.aiproject.ics.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UsersRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users=repository.findByUserName(username).orElse(null);
        if (users!=null){
            return  org.springframework.security.core.userdetails.User
                    .withUsername(users.getUsername())
                    .password(users.getPassword())
                    .roles("USER")
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();

        }else {
            throw new UsernameNotFoundException("User doesn't exists with this username "+username);
        }
        }
    }

