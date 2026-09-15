package com.vehiclerental.dao;

import com.vehiclerental.model.Review;
import com.vehiclerental.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    
    // Create review
    public int createReview(Review review) {
        String sql = "INSERT INTO reviews (user_id, vehicle_id, booking_id, rating, comment, status) VALUES (?, ?, ?, ?, ?, 'pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getUserId());
            ps.setInt(2, review.getVehicleId());
            ps.setInt(3, review.getBookingId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());
            
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Get review by ID
    public Review getReviewById(int reviewId) {
        String sql = "SELECT r.*, u.name as user_name, v.name as vehicle_name FROM reviews r JOIN users u ON r.user_id = u.id JOIN vehicles v ON r.vehicle_id = v.id WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToReview(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get reviews by vehicle
    public List<Review> getReviewsByVehicleId(int vehicleId) {
        String sql = "SELECT r.*, u.name as user_name, v.name as vehicle_name FROM reviews r JOIN users u ON r.user_id = u.id JOIN vehicles v ON r.vehicle_id = v.id WHERE r.vehicle_id = ? AND r.status = 'approved' ORDER BY r.created_at DESC";
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // Get all reviews (admin)
    public List<Review> getAllReviews() {
        String sql = "SELECT r.*, u.name as user_name, v.name as vehicle_name FROM reviews r JOIN users u ON r.user_id = u.id JOIN vehicles v ON r.vehicle_id = v.id ORDER BY r.created_at DESC";
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // Get pending reviews (admin)
    public List<Review> getPendingReviews() {
        String sql = "SELECT r.*, u.name as user_name, v.name as vehicle_name FROM reviews r JOIN users u ON r.user_id = u.id JOIN vehicles v ON r.vehicle_id = v.id WHERE r.status = 'pending' ORDER BY r.created_at DESC";
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // Check if user already reviewed
    public boolean hasUserReviewedVehicle(int userId, int vehicleId) {
        String sql = "SELECT id FROM reviews WHERE user_id = ? AND vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, vehicleId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Approve review
    public boolean approveReview(int reviewId) {
        String sql = "UPDATE reviews SET status = 'approved' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Reject review
    public boolean rejectReview(int reviewId) {
        String sql = "UPDATE reviews SET status = 'rejected' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete review
    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get average rating for vehicle
    public double getAverageRating(int vehicleId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM reviews WHERE vehicle_id = ? AND status = 'approved'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double avg = rs.getDouble("avg_rating");
                return avg > 0 ? Math.round(avg * 100.0) / 100.0 : 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Helper method
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setUserId(rs.getInt("user_id"));
        review.setVehicleId(rs.getInt("vehicle_id"));
        review.setBookingId(rs.getInt("booking_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setStatus(rs.getString("status"));
        review.setCreatedAt(rs.getString("created_at"));
        review.setUserName(rs.getString("user_name"));
        review.setVehicleName(rs.getString("vehicle_name"));
        return review;
    }
}
