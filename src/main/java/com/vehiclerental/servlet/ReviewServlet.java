package com.vehiclerental.servlet;

import com.vehiclerental.dao.ReviewDAO;
import com.vehiclerental.dao.BookingDAO;
import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.model.Review;
import com.vehiclerental.model.Booking;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/submit-review")
public class ReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReviewDAO reviewDAO = new ReviewDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingIdStr = request.getParameter("bookingId");
        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/my-bookings");
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdStr);
            int userId = SessionUtil.getCurrentUserId(session);
            Booking booking = bookingDAO.getBookingById(bookingId);

            if (booking == null || booking.getUserId() != userId) {
                response.sendRedirect(request.getContextPath() + "/my-bookings");
                return;
            }

            if (!"Completed".equals(booking.getBookingStatus())) {
                response.sendRedirect(request.getContextPath() + "/my-bookings?error=Can+only+review+completed+bookings");
                return;
            }

            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/submit-review.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingIdStr = request.getParameter("bookingId");
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");
        int userId = SessionUtil.getCurrentUserId(session);

        String error = null;

        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            error = "Booking ID is required";
        } else if (ratingStr == null || ratingStr.isEmpty()) {
            error = "Rating is required";
        } else {
            try {
                int bookingId = Integer.parseInt(bookingIdStr);
                int rating = Integer.parseInt(ratingStr);
                Booking booking = bookingDAO.getBookingById(bookingId);

                if (booking == null || booking.getUserId() != userId) {
                    error = "Invalid booking";
                } else if (rating < 1 || rating > 5) {
                    error = "Rating must be between 1 and 5";
                } else {
                    Review review = new Review(userId, booking.getVehicleId(), bookingId, rating, comment);
                    int reviewId = reviewDAO.createReview(review);

                    if (reviewId > 0) {
                        // Update vehicle rating
                        double avgRating = reviewDAO.getAverageRating(booking.getVehicleId());
                        vehicleDAO.updateRating(booking.getVehicleId(), avgRating);
                        response.sendRedirect(request.getContextPath() + "/my-bookings?success=Review+submitted+successfully");
                        return;
                    } else {
                        error = "Failed to submit review";
                    }
                }
            } catch (NumberFormatException e) {
                error = "Invalid input";
            }
        }

        request.setAttribute("error", error);
        request.setAttribute("bookingId", bookingIdStr);
        request.getRequestDispatcher("/submit-review.jsp").forward(request, response);
    }
}
