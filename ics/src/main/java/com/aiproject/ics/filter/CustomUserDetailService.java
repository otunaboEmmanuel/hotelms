package com.aiproject.ics.filter;

import com.aiproject.ics.entity.Users;
import com.aiproject.ics.repository.jpa.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UsersRepository repository;

    public CustomUserDetailService(UsersRepository repository) {
        this.repository = repository;
    }

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

