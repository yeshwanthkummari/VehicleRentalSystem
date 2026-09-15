package com.vehiclerental.servlet;

import com.vehiclerental.dao.BookingDAO;
import com.vehiclerental.dao.PaymentDAO;
import com.vehiclerental.model.Booking;
import com.vehiclerental.model.Payment;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookingDAO bookingDAO = new BookingDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Booking booking = (Booking) session.getAttribute("bookingData");
        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }

        request.setAttribute("booking", booking);
        request.getRequestDispatcher("/payment.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = SessionUtil.getCurrentUserId(session);
        Booking booking = (Booking) session.getAttribute("bookingData");

        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }

        String paymentMethod = request.getParameter("paymentMethod");

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            request.setAttribute("error", "Payment method is required");
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/payment.jsp").forward(request, response);
            return;
        }

        // Create booking
        int bookingId = bookingDAO.createBooking(booking);
        if (bookingId <= 0) {
            request.setAttribute("error", "Failed to create booking");
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/payment.jsp").forward(request, response);
            return;
        }

        // Create payment record
        Payment payment = new Payment(bookingId, userId, booking.getTotalAmount(), paymentMethod);
        String transactionId = "TXN-" + UUID.randomUUID().toString();
        payment.setTransactionReference(transactionId);
        payment.setPaymentStatus("Paid");

        int paymentId = paymentDAO.createPayment(payment);
        if (paymentId <= 0) {
            request.setAttribute("error", "Payment processing failed");
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/payment.jsp").forward(request, response);
            return;
        }

        // Update booking status
        bookingDAO.updateBookingStatus(bookingId, "Confirmed");

        // Clear session
        session.removeAttribute("bookingData");
        session.removeAttribute("bookingVehicle");

        // Redirect to success page
        request.setAttribute("bookingId", bookingId);
        request.setAttribute("paymentId", paymentId);
        request.setAttribute("payment", payment);
        request.getRequestDispatcher("/payment-success.jsp").forward(request, response);
    }
}
