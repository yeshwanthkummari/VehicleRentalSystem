package com.vehiclerental.servlet;

import com.vehiclerental.dao.BookingDAO;
import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.model.Booking;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.util.SessionUtil;
import com.vehiclerental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@WebServlet("/booking")
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String vehicleIdStr = request.getParameter("id");
        if (vehicleIdStr == null || vehicleIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(vehicleIdStr);
            Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
            
            if (vehicle == null || !vehicle.isAvailable()) {
                request.setAttribute("error", "Vehicle is not available");
                request.getRequestDispatcher("/vehicle-details?id=" + vehicleId).forward(request, response);
                return;
            }

            request.setAttribute("vehicle", vehicle);
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/vehicles");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = SessionUtil.getCurrentUserId(session);
        String vehicleIdStr = request.getParameter("vehicleId");
        String pickupDate = request.getParameter("pickupDate");
        String pickupTime = request.getParameter("pickupTime");
        String returnDate = request.getParameter("returnDate");
        String returnTime = request.getParameter("returnTime");

        String error = null;

        if (vehicleIdStr == null || vehicleIdStr.isEmpty()) {
            error = "Vehicle is required";
        } else if (pickupDate == null || pickupDate.isEmpty()) {
            error = "Pickup date is required";
        } else if (!ValidationUtil.isValidDate(pickupDate)) {
            error = "Invalid pickup date format";
        } else if (!ValidationUtil.isFutureDate(pickupDate)) {
            error = "Pickup date must be in future";
        } else if (returnDate == null || returnDate.isEmpty()) {
            error = "Return date is required";
        } else if (!ValidationUtil.isValidDate(returnDate)) {
            error = "Invalid return date format";
        } else if (!ValidationUtil.isReturnDateAfterPickup(pickupDate, returnDate)) {
            error = "Return date must be same or after pickup date";
        } else if (pickupTime == null || pickupTime.isEmpty()) {
            error = "Pickup time is required";
        } else if (returnTime == null || returnTime.isEmpty()) {
            error = "Return time is required";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("vehicleId", vehicleIdStr);
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
            return;
        }

        try {
            int vehicleId = Integer.parseInt(vehicleIdStr);
            Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);

            if (vehicle == null || !vehicle.isAvailable()) {
                request.setAttribute("error", "Vehicle is not available");
                request.getRequestDispatcher("/booking.jsp").forward(request, response);
                return;
            }

            // Check availability
            if (!bookingDAO.isVehicleAvailable(vehicleId, pickupDate, returnDate, pickupTime, returnTime)) {
                request.setAttribute("error", "Vehicle is not available for selected dates");
                request.getRequestDispatcher("/booking.jsp").forward(request, response);
                return;
            }

            // Calculate duration and total amount
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            LocalDate pickup = LocalDate.parse(pickupDate, dateFormatter);
            LocalDate ret = LocalDate.parse(returnDate, dateFormatter);
            LocalTime pickTime = LocalTime.parse(pickupTime, timeFormatter);
            LocalTime retTime = LocalTime.parse(returnTime, timeFormatter);

            long days = ChronoUnit.DAYS.between(pickup, ret);
            int hours = (int) ChronoUnit.HOURS.between(pickTime, retTime);
            
            if (hours < 0) {
                hours += 24;
            }

            double totalAmount = 0;
            if (days > 0) {
                totalAmount = days * vehicle.getPricePerDay() + (hours * vehicle.getPricePerHour());
            } else {
                totalAmount = hours * vehicle.getPricePerHour();
            }

            Booking booking = new Booking(userId, vehicleId, pickupDate, pickupTime, returnDate, returnTime, totalAmount);
            booking.setDurationHours(hours);
            booking.setDurationDays((int) days);

            // Store in session for confirmation
            session.setAttribute("bookingData", booking);
            session.setAttribute("bookingVehicle", vehicle);

            request.setAttribute("booking", booking);
            request.setAttribute("vehicle", vehicle);
            request.getRequestDispatcher("/booking-confirmation.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid vehicle ID");
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
        }
    }
}
