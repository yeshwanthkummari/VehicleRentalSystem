package com.vehiclerental.servlet.admin;

import com.vehiclerental.dao.ReviewDAO;
import com.vehiclerental.model.Review;
import com.vehiclerental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/reviews")
public class AdminReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReviewDAO reviewDAO = new ReviewDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Review> reviews = reviewDAO.getAllReviews();
        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("/admin/reviews.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        if (!SessionUtil.isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String reviewIdStr = request.getParameter("reviewId");

        if (reviewIdStr != null) {
            try {
                int reviewId = Integer.parseInt(reviewIdStr);
                if ("approve".equals(action)) {
                    reviewDAO.approveReview(reviewId);
                } else if ("reject".equals(action)) {
                    reviewDAO.rejectReview(reviewId);
                } else if ("delete".equals(action)) {
                    reviewDAO.deleteReview(reviewId);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/reviews");
    }
}
