package com.vehiclerental.servlet;

import com.vehiclerental.dao.CategoryDAO;
import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.model.Category;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        List<Vehicle> featuredVehicles = vehicleDAO.getAvailableVehicles();
        List<Category> categories = categoryDAO.getAllCategories();
        
        request.setAttribute("featuredVehicles", featuredVehicles);
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
