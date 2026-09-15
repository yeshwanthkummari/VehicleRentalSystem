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

@WebServlet("/vehicles")
public class VehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VehicleDAO vehicleDAO = new VehicleDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryId = request.getParameter("category");
        List<Vehicle> vehicles;

        if (categoryId != null && !categoryId.isEmpty()) {
            try {
                vehicles = vehicleDAO.getVehiclesByCategory(Integer.parseInt(categoryId));
            } catch (NumberFormatException e) {
                vehicles = vehicleDAO.getAllVehicles();
            }
        } else {
            vehicles = vehicleDAO.getAllVehicles();
        }

        request.setAttribute("vehicles", vehicles);
        request.getRequestDispatcher("/vehicles.jsp").forward(request, response);
    }
}
