package com.vehiclerental.util;

import com.vehiclerental.model.User;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    private static final String USER_SESSION_KEY = "user";
    private static final String SESSION_TIMEOUT = "1800"; // 30 minutes in seconds

    // Set user in session
    public static void setUserSession(HttpSession session, User user) {
        if (session != null && user != null) {
            session.setAttribute(USER_SESSION_KEY, user);
            session.setMaxInactiveInterval(1800); // 30 minutes
        }
    }

    // Get user from session
    public static User getUserSession(HttpSession session) {
        if (session != null) {
            return (User) session.getAttribute(USER_SESSION_KEY);
        }
        return null;
    }

    // Check if user is logged in
    public static boolean isUserLoggedIn(HttpSession session) {
        return session != null && getUserSession(session) != null;
    }

    // Check if user is admin
    public static boolean isAdmin(HttpSession session) {
        User user = getUserSession(session);
        return user != null && user.isAdmin();
    }

    // Logout user
    public static void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    // Get current user ID
    public static int getCurrentUserId(HttpSession session) {
        User user = getUserSession(session);
        return user != null ? user.getId() : -1;
    }
}
