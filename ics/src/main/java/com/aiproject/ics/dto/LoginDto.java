package com.aiproject.ics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @JsonProperty("email")
    private String email;
    private String password;
    private Integer adminId;
    @JsonProperty("username")
    private String userName;
    private Integer bookId;
    private Integer userId;

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public Integer getAdminId() {
//        return adminId;
//    }
//
//    public void setAdminId(Integer adminId) {
//        this.adminId = adminId;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public Integer getBookId() {
//        return bookId;
//    }
//
//    public void setBookId(Integer bookId) {
//        this.bookId = bookId;
//    }
//
//    public Integer getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Integer userId) {
//        this.userId = userId;
//    }
}
