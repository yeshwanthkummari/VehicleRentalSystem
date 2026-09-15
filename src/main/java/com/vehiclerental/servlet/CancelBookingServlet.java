package com.vehiclerental.servlet;

import com.vehiclerental.dao.BookingDAO;
import com.vehiclerental.dao.PaymentDAO;
import com.vehiclerental.model.Booking;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/cancel-booking")
public class CancelBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookingDAO bookingDAO = new BookingDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingIdStr = request.getParameter("bookingId");
        int userId = SessionUtil.getCurrentUserId(session);

        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/my-bookings");
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdStr);
            Booking booking = bookingDAO.getBookingById(bookingId);

            if (booking == null || booking.getUserId() != userId) {
                response.sendRedirect(request.getContextPath() + "/my-bookings");
                return;
            }

            if ("Completed".equals(booking.getBookingStatus()) || "Cancelled".equals(booking.getBookingStatus())) {
                response.sendRedirect(request.getContextPath() + "/my-bookings?error=Cannot+cancel+this+booking");
                return;
            }

            // Cancel booking
            bookingDAO.cancelBooking(bookingId);

            // Update payment status to Refunded
            paymentDAO.updatePaymentStatusByBookingId(bookingId, "Refunded");

            response.sendRedirect(request.getContextPath() + "/my-bookings?success=Booking+cancelled+successfully");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        }
    }
}
