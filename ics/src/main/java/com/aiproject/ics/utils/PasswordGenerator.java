package com.aiproject.ics.utils;


import java.util.UUID;

public class PasswordGenerator {
    public static String generatePassword(){
        String randomPassword= UUID.randomUUID().toString();
        String random=randomPassword.replaceAll("-","");
        return random;
    }
}
