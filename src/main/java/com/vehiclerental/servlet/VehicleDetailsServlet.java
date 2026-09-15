package com.vehiclerental.servlet;

import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.dao.ReviewDAO;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.Review;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/vehicle-details")
public class VehicleDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vehicleIdStr = request.getParameter("id");
        
        if (vehicleIdStr == null || vehicleIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(vehicleIdStr);
            Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
            
            if (vehicle == null) {
                response.sendRedirect(request.getContextPath() + "/vehicles");
                return;
            }

            List<Review> reviews = reviewDAO.getReviewsByVehicleId(vehicleId);
            double avgRating = reviewDAO.getAverageRating(vehicleId);

            request.setAttribute("vehicle", vehicle);
            request.setAttribute("reviews", reviews);
            request.setAttribute("avgRating", avgRating);
            request.getRequestDispatcher("/vehicle-details.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/vehicles");
        }
    }
}
