package com.vehiclerental.servlet.admin;

import com.vehiclerental.dao.PaymentDAO;
import com.vehiclerental.model.Payment;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/payments")
public class AdminPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PaymentDAO paymentDAO = new PaymentDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Payment> payments = paymentDAO.getAllPayments();
        request.setAttribute("payments", payments);
        request.getRequestDispatcher("/admin/payments.jsp").forward(request, response);
    }
}
