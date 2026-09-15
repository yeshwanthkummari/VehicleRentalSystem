package com.vehiclerental.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^[6-9]\\d{9}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    // Validate email format
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Validate phone number (Indian format)
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    // Validate name
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 3;
    }

    // Validate address
    public static boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty() && address.length() >= 5;
    }

    // Check if date is in future
    public static boolean isFutureDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateStr, formatter);
            return date.isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    // Validate date format
    public static boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Compare two dates
    public static boolean isReturnDateAfterPickup(String pickupDate, String returnDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate pickup = LocalDate.parse(pickupDate, formatter);
            LocalDate ret = LocalDate.parse(returnDate, formatter);
            return ret.isAfter(pickup) || ret.isEqual(pickup);
        } catch (Exception e) {
            return false;
        }
    }

    // Validate amount
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    // Sanitize input to prevent XSS
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>\"']", "");
    }
}
