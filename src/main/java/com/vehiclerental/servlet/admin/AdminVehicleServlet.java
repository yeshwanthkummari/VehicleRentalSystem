package com.vehiclerental.servlet.admin;

import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.dao.CategoryDAO;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.Category;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/vehicles")
public class AdminVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/admin/add-vehicle.jsp").forward(request, response);
        } else if ("edit".equals(action)) {
            String vehicleIdStr = request.getParameter("id");
            if (vehicleIdStr != null) {
                try {
                    int vehicleId = Integer.parseInt(vehicleIdStr);
                    Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
                    List<Category> categories = categoryDAO.getAllCategories();
                    request.setAttribute("vehicle", vehicle);
                    request.setAttribute("categories", categories);
                    request.getRequestDispatcher("/admin/edit-vehicle.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/vehicles");
                }
            }
        } else {
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            request.setAttribute("vehicles", vehicles);
            request.getRequestDispatcher("/admin/vehicles.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            addVehicle(request, response);
        } else if ("update".equals(action)) {
            updateVehicle(request, response);
        } else if ("delete".equals(action)) {
            deleteVehicle(request, response);
        } else if ("updateStatus".equals(action)) {
            updateStatus(request, response);
        }
    }

    private void addVehicle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setName(request.getParameter("name"));
            vehicle.setBrand(request.getParameter("brand"));
            vehicle.setModel(request.getParameter("model"));
            vehicle.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            vehicle.setRegistrationNumber(request.getParameter("registrationNumber"));
            vehicle.setFuelType(request.getParameter("fuelType"));
            vehicle.setTransmission(request.getParameter("transmission"));
            vehicle.setSeatingCapacity(Integer.parseInt(request.getParameter("seatingCapacity")));
            vehicle.setPricePerHour(Double.parseDouble(request.getParameter("pricePerHour")));
            vehicle.setPricePerDay(Double.parseDouble(request.getParameter("pricePerDay")));
            vehicle.setMileage(request.getParameter("mileage"));
            vehicle.setImage(request.getParameter("image"));
            vehicle.setDescription(request.getParameter("description"));
            vehicle.setAvailabilityStatus("Available");

            vehicleDAO.addVehicle(vehicle);
            response.sendRedirect(request.getContextPath() + "/admin/vehicles?success=Vehicle+added");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/vehicles?error=Failed+to+add+vehicle");
        }
    }

    private void updateVehicle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            Vehicle vehicle = new Vehicle();
            vehicle.setId(vehicleId);
            vehicle.setName(request.getParameter("name"));
            vehicle.setBrand(request.getParameter("brand"));
            vehicle.setModel(request.getParameter("model"));
            vehicle.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            vehicle.setRegistrationNumber(request.getParameter("registrationNumber"));
            vehicle.setFuelType(request.getParameter("fuelType"));
            vehicle.setTransmission(request.getParameter("transmission"));
            vehicle.setSeatingCapacity(Integer.parseInt(request.getParameter("seatingCapacity")));
            vehicle.setPricePerHour(Double.parseDouble(request.getParameter("pricePerHour")));
            vehicle.setPricePerDay(Double.parseDouble(request.getParameter("pricePerDay")));
            vehicle.setMileage(request.getParameter("mileage"));
            vehicle.setImage(request.getParameter("image"));
            vehicle.setDescription(request.getParameter("description"));

            vehicleDAO.updateVehicle(vehicle);
            response.sendRedirect(request.getContextPath() + "/admin/vehicles?success=Vehicle+updated");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/vehicles?error=Failed+to+update+vehicle");
        }
    }

    private void deleteVehicle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            vehicleDAO.deleteVehicle(vehicleId);
            response.sendRedirect(request.getContextPath() + "/admin/vehicles?success=Vehicle+deleted");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/vehicles?error=Failed+to+delete+vehicle");
        }
    }

    private void updateStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            String status = request.getParameter("status");
            vehicleDAO.updateAvailabilityStatus(vehicleId, status);
            response.sendRedirect(request.getContextPath() + "/admin/vehicles?success=Status+updated");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/vehicles?error=Failed+to+update+status");
        }
    }
}
