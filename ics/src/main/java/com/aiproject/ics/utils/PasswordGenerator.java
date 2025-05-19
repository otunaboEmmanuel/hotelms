package com.aiproject.ics.utils;


import java.util.Random;
import java.util.UUID;

public class PasswordGenerator {
    public static String generatePassword(){
        String randomPassword= UUID.randomUUID().toString();
        String random=randomPassword.replaceAll("-","");
        return random;
    }
    public  static Integer random(){
        Random random=new Random();
        return random.nextInt(100000);
    }
}
