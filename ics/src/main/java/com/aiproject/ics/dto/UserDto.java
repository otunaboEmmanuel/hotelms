package com.aiproject.ics.dto;

import com.aiproject.ics.entity.Users;
import com.aiproject.ics.enums.Roles;

public class UserDto {
    private Integer id;
    private String userName;
    private Roles role;
    private String email;

    public UserDto(Users users) {
        this.id=users.getId();
        this.userName = users.getUsername();
        this.role = users.getRole();
        this.email = users.getEmail();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
