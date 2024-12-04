package com.example.backend.services;

import java.util.regex.Pattern;

public class PatternMatch {
    final String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public boolean emailCheck(String text) {
        System.out.println(text);
        return Pattern.compile(emailRegex).matcher(text).matches();
    }
}
