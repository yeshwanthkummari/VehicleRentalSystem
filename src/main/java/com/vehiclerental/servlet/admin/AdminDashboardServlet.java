package com.vehiclerental.servlet.admin;

import com.vehiclerental.dao.*;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int totalUsers = userDAO.getTotalUsers();
        int totalVehicles = vehicleDAO.getTotalVehicles();
        int availableVehicles = vehicleDAO.getAvailableVehiclesCount();
        int totalBookings = bookingDAO.getTotalBookings();
        int pendingBookings = bookingDAO.getPendingBookingsCount();
        int completedBookings = bookingDAO.getCompletedBookingsCount();
        int cancelledBookings = bookingDAO.getCancelledBookingsCount();
        double totalRevenue = paymentDAO.getTotalRevenue();

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalVehicles", totalVehicles);
        request.setAttribute("availableVehicles", availableVehicles);
        request.setAttribute("bookedVehicles", totalVehicles - availableVehicles);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("pendingBookings", pendingBookings);
        request.setAttribute("completedBookings", completedBookings);
        request.setAttribute("cancelledBookings", cancelledBookings);
        request.setAttribute("totalRevenue", String.format("%.2f", totalRevenue));

        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}
