package com.vehiclerental.servlet.admin;

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

@WebServlet("/admin/bookings")
public class AdminBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookingDAO bookingDAO = new BookingDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Booking> bookings = bookingDAO.getAllBookings();
        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/admin/bookings.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingIdStr = request.getParameter("bookingId");
        String status = request.getParameter("status");

        if (bookingIdStr != null && status != null) {
            try {
                int bookingId = Integer.parseInt(bookingIdStr);
                bookingDAO.updateBookingStatus(bookingId, status);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/bookings");
    }
}
