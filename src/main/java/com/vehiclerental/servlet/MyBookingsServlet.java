package com.vehiclerental.servlet;

import com.vehiclerental.dao.BookingDAO;
import com.vehiclerental.model.Booking;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/my-bookings")
public class MyBookingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookingDAO bookingDAO = new BookingDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = SessionUtil.getCurrentUserId(session);
        List<Booking> bookings = bookingDAO.getBookingsByUserId(userId);

        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/my-bookings.jsp").forward(request, response);
    }
}
