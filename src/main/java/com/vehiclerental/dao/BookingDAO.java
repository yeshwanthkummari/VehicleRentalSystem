package com.vehiclerental.dao;

import com.vehiclerental.model.Booking;
import com.vehiclerental.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    
    // Create booking
    public int createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, vehicle_id, pickup_date, pickup_time, return_date, return_time, duration_hours, duration_days, total_amount, booking_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getVehicleId());
            ps.setString(3, booking.getPickupDate());
            ps.setString(4, booking.getPickupTime());
            ps.setString(5, booking.getReturnDate());
            ps.setString(6, booking.getReturnTime());
            ps.setInt(7, booking.getDurationHours());
            ps.setInt(8, booking.getDurationDays());
            ps.setDouble(9, booking.getTotalAmount());
            
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

    // Get booking by ID
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT b.*, u.name as user_name, v.name as vehicle_name, p.payment_status FROM bookings b JOIN users u ON b.user_id = u.id JOIN vehicles v ON b.vehicle_id = v.id LEFT JOIN payments p ON b.id = p.booking_id WHERE b.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToBooking(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all bookings by user
    public List<Booking> getBookingsByUserId(int userId) {
        String sql = "SELECT b.*, u.name as user_name, v.name as vehicle_name, p.payment_status FROM bookings b JOIN users u ON b.user_id = u.id JOIN vehicles v ON b.vehicle_id = v.id LEFT JOIN payments p ON b.id = p.booking_id WHERE b.user_id = ? ORDER BY b.created_at DESC";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Get all bookings (admin)
    public List<Booking> getAllBookings() {
        String sql = "SELECT b.*, u.name as user_name, v.name as vehicle_name, p.payment_status FROM bookings b JOIN users u ON b.user_id = u.id JOIN vehicles v ON b.vehicle_id = v.id LEFT JOIN payments p ON b.id = p.booking_id ORDER BY b.created_at DESC";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Check booking overlap
    public boolean isVehicleAvailable(int vehicleId, String pickupDate, String returnDate, String pickupTime, String returnTime) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE vehicle_id = ? AND booking_status != 'Cancelled' AND ((pickup_date <= ? AND return_date >= ?) OR (pickup_date <= ? AND return_date >= ?) OR (pickup_date >= ? AND return_date <= ?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setString(2, returnDate);
            ps.setString(3, pickupDate);
            ps.setString(4, pickupDate);
            ps.setString(5, returnDate);
            ps.setString(6, pickupDate);
            ps.setString(7, returnDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update booking status
    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET booking_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cancel booking
    public boolean cancelBooking(int bookingId) {
        return updateBookingStatus(bookingId, "Cancelled");
    }

    // Get total bookings
    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) as total FROM bookings";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get pending bookings count
    public int getPendingBookingsCount() {
        String sql = "SELECT COUNT(*) as total FROM bookings WHERE booking_status = 'Pending'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get completed bookings count
    public int getCompletedBookingsCount() {
        String sql = "SELECT COUNT(*) as total FROM bookings WHERE booking_status = 'Completed'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get cancelled bookings count
    public int getCancelledBookingsCount() {
        String sql = "SELECT COUNT(*) as total FROM bookings WHERE booking_status = 'Cancelled'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setVehicleId(rs.getInt("vehicle_id"));
        booking.setPickupDate(rs.getString("pickup_date"));
        booking.setPickupTime(rs.getString("pickup_time"));
        booking.setReturnDate(rs.getString("return_date"));
        booking.setReturnTime(rs.getString("return_time"));
        booking.setDurationHours(rs.getInt("duration_hours"));
        booking.setDurationDays(rs.getInt("duration_days"));
        booking.setTotalAmount(rs.getDouble("total_amount"));
        booking.setBookingStatus(rs.getString("booking_status"));
        booking.setCreatedAt(rs.getString("created_at"));
        booking.setUserName(rs.getString("user_name"));
        booking.setVehicleName(rs.getString("vehicle_name"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        return booking;
    }
}
