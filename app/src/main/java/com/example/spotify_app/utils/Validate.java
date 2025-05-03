package com.example.spotify_app.utils;

public class Validate {
    public boolean validateEmail(String email) {
        String regex = "^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$";
        return email.matches(regex);
    }

    public boolean validatePhoneNumber(String phoneNumber) {
        String regex = "^0\\d{9}$";
        return phoneNumber.matches(regex);
    }

    public boolean validatePassword(String password) {
        String regex = "^((?=\\S*?[A-Z])(?=\\S*?[a-z])(?=\\S*?[0-9])(?=\\S*?[^\\w\\s]).{6,})\\S$";
        return password.matches(regex);
    }
}
