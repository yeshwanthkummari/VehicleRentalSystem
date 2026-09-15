package com.vehiclerental.servlet;

import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.model.Vehicle;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/search-vehicles")
public class SearchVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VehicleDAO vehicleDAO = new VehicleDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Vehicle> vehicles;

        if (keyword != null && !keyword.trim().isEmpty()) {
            vehicles = vehicleDAO.searchVehicles(keyword);
        } else {
            vehicles = vehicleDAO.getAllVehicles();
        }

        request.setAttribute("vehicles", vehicles);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/vehicles.jsp").forward(request, response);
    }
}
