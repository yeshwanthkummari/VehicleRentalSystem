package com.vehiclerental.servlet;

import com.vehiclerental.dao.UserDAO;
import com.vehiclerental.model.User;
import com.vehiclerental.util.PasswordUtil;
import com.vehiclerental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validation
        String error = null;

        if (name == null || name.trim().isEmpty()) {
            error = "Name is required";
        } else if (!ValidationUtil.isValidName(name)) {
            error = "Name must be at least 3 characters";
        } else if (email == null || email.trim().isEmpty()) {
            error = "Email is required";
        } else if (!ValidationUtil.isValidEmail(email)) {
            error = "Invalid email format";
        } else if (userDAO.emailExists(email)) {
            error = "Email already registered";
        } else if (phone == null || phone.trim().isEmpty()) {
            error = "Phone number is required";
        } else if (!ValidationUtil.isValidPhone(phone)) {
            error = "Invalid phone number (10 digits required)";
        } else if (address == null || address.trim().isEmpty()) {
            error = "Address is required";
        } else if (!ValidationUtil.isValidAddress(address)) {
            error = "Address must be at least 5 characters";
        } else if (password == null || password.trim().isEmpty()) {
            error = "Password is required";
        } else if (!PasswordUtil.isValidPassword(password)) {
            error = "Password must be at least 6 characters with letters and numbers";
        } else if (!password.equals(confirmPassword)) {
            error = "Passwords do not match";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        User user = new User(name, email, phone, address, PasswordUtil.hashPassword(password));
        if (userDAO.registerUser(user)) {
            request.setAttribute("success", "Registration successful. Please login.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
