package com.vehiclerental.servlet;

import com.vehiclerental.dao.UserDAO;
import com.vehiclerental.model.User;
import com.vehiclerental.util.PasswordUtil;
import com.vehiclerental.util.SessionUtil;
import com.vehiclerental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = SessionUtil.getUserSession(session);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = SessionUtil.getUserSession(session);
        String action = request.getParameter("action");
        String error = null;
        String success = null;

        if ("updateProfile".equals(action)) {
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");

            if (name == null || name.trim().isEmpty()) {
                error = "Name is required";
            } else if (!ValidationUtil.isValidName(name)) {
                error = "Name must be at least 3 characters";
            } else if (phone == null || phone.trim().isEmpty()) {
                error = "Phone is required";
            } else if (!ValidationUtil.isValidPhone(phone)) {
                error = "Invalid phone number";
            } else if (address == null || address.trim().isEmpty()) {
                error = "Address is required";
            } else if (!ValidationUtil.isValidAddress(address)) {
                error = "Address must be at least 5 characters";
            }

            if (error == null) {
                if (userDAO.updateUserProfile(user.getId(), name, phone, address)) {
                    user.setName(name);
                    user.setPhone(phone);
                    user.setAddress(address);
                    SessionUtil.setUserSession(session, user);
                    success = "Profile updated successfully";
                } else {
                    error = "Failed to update profile";
                }
            }
        } else if ("changePassword".equals(action)) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            if (currentPassword == null || currentPassword.isEmpty()) {
                error = "Current password is required";
            } else if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
                error = "Current password is incorrect";
            } else if (newPassword == null || newPassword.isEmpty()) {
                error = "New password is required";
            } else if (!PasswordUtil.isValidPassword(newPassword)) {
                error = "Password must be at least 6 characters with letters and numbers";
            } else if (!newPassword.equals(confirmPassword)) {
                error = "Passwords do not match";
            }

            if (error == null) {
                if (userDAO.updatePassword(user.getId(), PasswordUtil.hashPassword(newPassword))) {
                    success = "Password changed successfully";
                } else {
                    error = "Failed to change password";
                }
            }
        }

        request.setAttribute("user", user);
        if (error != null) {
            request.setAttribute("error", error);
        }
        if (success != null) {
            request.setAttribute("success", success);
        }
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }
}
