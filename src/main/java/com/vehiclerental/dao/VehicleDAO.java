package com.vehiclerental.dao;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    
    // Get all vehicles
    public List<Vehicle> getAllVehicles() {
        String sql = "SELECT v.*, c.name as category_name FROM vehicles v JOIN categories c ON v.category_id = c.id ORDER BY v.created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // Get vehicle by ID
    public Vehicle getVehicleById(int vehicleId) {
        String sql = "SELECT v.*, c.name as category_name FROM vehicles v JOIN categories c ON v.category_id = c.id WHERE v.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToVehicle(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get vehicles by category
    public List<Vehicle> getVehiclesByCategory(int categoryId) {
        String sql = "SELECT v.*, c.name as category_name FROM vehicles v JOIN categories c ON v.category_id = c.id WHERE v.category_id = ? ORDER BY v.created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // Search vehicles
    public List<Vehicle> searchVehicles(String keyword) {
        String sql = "SELECT v.*, c.name as category_name FROM vehicles v JOIN categories c ON v.category_id = c.id WHERE v.name LIKE ? OR v.brand LIKE ? OR v.model LIKE ? ORDER BY v.created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchTerm = "%" + keyword + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setString(3, searchTerm);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // Get available vehicles
    public List<Vehicle> getAvailableVehicles() {
        String sql = "SELECT v.*, c.name as category_name FROM vehicles v JOIN categories c ON v.category_id = c.id WHERE v.availability_status = 'Available' ORDER BY v.created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // Add vehicle
    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (name, brand, model, category_id, registration_number, fuel_type, transmission, seating_capacity, price_per_hour, price_per_day, mileage, image, description, availability_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicle.getName());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getModel());
            ps.setInt(4, vehicle.getCategoryId());
            ps.setString(5, vehicle.getRegistrationNumber());
            ps.setString(6, vehicle.getFuelType());
            ps.setString(7, vehicle.getTransmission());
            ps.setInt(8, vehicle.getSeatingCapacity());
            ps.setDouble(9, vehicle.getPricePerHour());
            ps.setDouble(10, vehicle.getPricePerDay());
            ps.setString(11, vehicle.getMileage());
            ps.setString(12, vehicle.getImage());
            ps.setString(13, vehicle.getDescription());
            ps.setString(14, vehicle.getAvailabilityStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update vehicle
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET name = ?, brand = ?, model = ?, category_id = ?, registration_number = ?, fuel_type = ?, transmission = ?, seating_capacity = ?, price_per_hour = ?, price_per_day = ?, mileage = ?, image = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicle.getName());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getModel());
            ps.setInt(4, vehicle.getCategoryId());
            ps.setString(5, vehicle.getRegistrationNumber());
            ps.setString(6, vehicle.getFuelType());
            ps.setString(7, vehicle.getTransmission());
            ps.setInt(8, vehicle.getSeatingCapacity());
            ps.setDouble(9, vehicle.getPricePerHour());
            ps.setDouble(10, vehicle.getPricePerDay());
            ps.setString(11, vehicle.getMileage());
            ps.setString(12, vehicle.getImage());
            ps.setString(13, vehicle.getDescription());
            ps.setInt(14, vehicle.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update availability status
    public boolean updateAvailabilityStatus(int vehicleId, String status) {
        String sql = "UPDATE vehicles SET availability_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete vehicle
    public boolean deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update rating
    public boolean updateRating(int vehicleId, double newRating) {
        String sql = "UPDATE vehicles SET rating = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newRating);
            ps.setInt(2, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get total vehicles count
    public int getTotalVehicles() {
        String sql = "SELECT COUNT(*) as total FROM vehicles";
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

    // Get available vehicles count
    public int getAvailableVehiclesCount() {
        String sql = "SELECT COUNT(*) as total FROM vehicles WHERE availability_status = 'Available'";
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

    // Helper method to map ResultSet to Vehicle
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getInt("id"));
        vehicle.setName(rs.getString("name"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setCategoryId(rs.getInt("category_id"));
        vehicle.setCategoryName(rs.getString("category_name"));
        vehicle.setRegistrationNumber(rs.getString("registration_number"));
        vehicle.setFuelType(rs.getString("fuel_type"));
        vehicle.setTransmission(rs.getString("transmission"));
        vehicle.setSeatingCapacity(rs.getInt("seating_capacity"));
        vehicle.setPricePerHour(rs.getDouble("price_per_hour"));
        vehicle.setPricePerDay(rs.getDouble("price_per_day"));
        vehicle.setMileage(rs.getString("mileage"));
        vehicle.setImage(rs.getString("image"));
        vehicle.setDescription(rs.getString("description"));
        vehicle.setRating(rs.getDouble("rating"));
        vehicle.setAvailabilityStatus(rs.getString("availability_status"));
        vehicle.setCreatedAt(rs.getString("created_at"));
        return vehicle;
    }
}
