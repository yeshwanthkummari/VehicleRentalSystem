-- Vehicle Rental System Database
-- Create and populate database with all required tables

CREATE DATABASE IF NOT EXISTS vehicle_rental_system;
USE vehicle_rental_system;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    category_id INT NOT NULL,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    fuel_type ENUM('Petrol', 'Diesel', 'Electric', 'CNG') NOT NULL,
    transmission ENUM('Manual', 'Automatic') NOT NULL,
    seating_capacity INT NOT NULL,
    price_per_hour DECIMAL(10, 2) NOT NULL,
    price_per_day DECIMAL(10, 2) NOT NULL,
    mileage VARCHAR(50),
    image VARCHAR(255),
    description TEXT,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    availability_status ENUM('Available', 'Booked', 'Maintenance') DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_category (category_id),
    INDEX idx_availability (availability_status),
    INDEX idx_brand (brand)
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    pickup_date DATE NOT NULL,
    pickup_time TIME NOT NULL,
    return_date DATE NOT NULL,
    return_time TIME NOT NULL,
    duration_hours INT,
    duration_days INT,
    total_amount DECIMAL(10, 2) NOT NULL,
    booking_status ENUM('Pending', 'Confirmed', 'Completed', 'Cancelled') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    INDEX idx_user (user_id),
    INDEX idx_vehicle (vehicle_id),
    INDEX idx_status (booking_status),
    INDEX idx_dates (pickup_date, return_date)
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL UNIQUE,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('UPI', 'Credit Card', 'Debit Card', 'Cash on Pickup') NOT NULL,
    payment_status ENUM('Pending', 'Paid', 'Refunded') DEFAULT 'Pending',
    transaction_reference VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id),
    INDEX idx_booking (booking_id),
    INDEX idx_status (payment_status)
);

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    booking_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    INDEX idx_vehicle (vehicle_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status)
);

-- Insert admin account
INSERT INTO users (name, email, phone, address, password, role, status) 
VALUES ('Admin', 'admin@vehiclerental.com', '9876543210', '123 Admin Street', '$2a$10$admin123', 'admin', 'active');

-- Insert categories
INSERT INTO categories (name, description) VALUES 
('Cars', 'Compact and sedan vehicles for comfortable rides'),
('Bikes', 'Two-wheeler bikes for swift and economical travel'),
('Scooters', 'Automatic scooters for city commute');

-- Insert vehicle data - CARS
INSERT INTO vehicles (name, brand, model, category_id, registration_number, fuel_type, transmission, seating_capacity, price_per_hour, price_per_day, mileage, image, description, rating, availability_status) VALUES
('Swift', 'Maruti', 'Swift', 1, 'KA01AB0001', 'Petrol', 'Manual', 5, 150, 1000, '18 km/l', 'swift.jpg', 'Compact and fuel-efficient city car', 4.5, 'Available'),
('Baleno', 'Maruti', 'Baleno', 1, 'KA01AB0002', 'Petrol', 'Automatic', 5, 180, 1200, '20 km/l', 'baleno.jpg', 'Premium hatchback with modern features', 4.6, 'Available'),
('Creta', 'Hyundai', 'Creta', 1, 'KA01AB0003', 'Diesel', 'Automatic', 5, 220, 1500, '16 km/l', 'creta.jpg', 'Spacious SUV for family trips', 4.7, 'Available'),
('Verna', 'Hyundai', 'Verna', 1, 'KA01AB0004', 'Petrol', 'Automatic', 5, 200, 1300, '17 km/l', 'verna.jpg', 'Elegant sedan with great comfort', 4.4, 'Available'),
('Nexon', 'Tata', 'Nexon', 1, 'KA01AB0005', 'Diesel', 'Manual', 5, 210, 1400, '15 km/l', 'nexon.jpg', 'Compact SUV with excellent handling', 4.5, 'Available'),
('Punch', 'Tata', 'Punch', 1, 'KA01AB0006', 'Petrol', 'Manual', 5, 160, 1050, '19 km/l', 'punch.jpg', 'Affordable micro SUV', 4.3, 'Available'),
('Seltos', 'Kia', 'Seltos', 1, 'KA01AB0007', 'Diesel', 'Automatic', 5, 240, 1600, '17 km/l', 'seltos.jpg', 'Feature-rich mid-size SUV', 4.6, 'Available'),
('Fortuner', 'Toyota', 'Fortuner', 1, 'KA01AB0008', 'Diesel', 'Automatic', 7, 300, 2000, '12 km/l', 'fortuner.jpg', 'Premium 7-seater SUV for long trips', 4.8, 'Available'),
('Innova', 'Toyota', 'Innova', 1, 'KA01AB0009', 'Diesel', 'Manual', 7, 280, 1800, '14 km/l', 'innova.jpg', '7-seater MPV for family vacations', 4.7, 'Available'),
('Scorpio', 'Mahindra', 'Scorpio', 1, 'KA01AB0010', 'Diesel', 'Manual', 7, 290, 1900, '13 km/l', 'scorpio.jpg', 'Rugged SUV with powerful engine', 4.5, 'Available'),
('City', 'Honda', 'City', 1, 'KA01AB0011', 'Petrol', 'Automatic', 5, 190, 1250, '18 km/l', 'city.jpg', 'Popular mid-size sedan with reliability', 4.6, 'Available');

-- Insert vehicle data - BIKES
INSERT INTO vehicles (name, brand, model, category_id, registration_number, fuel_type, transmission, seating_capacity, price_per_hour, price_per_day, mileage, image, description, rating, availability_status) VALUES
('Classic 350', 'Royal Enfield', 'Classic 350', 2, 'KA02AB0001', 'Petrol', 'Manual', 2, 80, 500, '40 km/l', 'classic350.jpg', 'Iconic cruiser motorcycle with retro styling', 4.4, 'Available'),
('Hunter 350', 'Royal Enfield', 'Hunter 350', 2, 'KA02AB0002', 'Petrol', 'Manual', 2, 85, 520, '42 km/l', 'hunter350.jpg', 'Modern roadster with classic charm', 4.5, 'Available'),
('MT-15', 'Yamaha', 'MT-15', 2, 'KA02AB0003', 'Petrol', 'Manual', 2, 90, 550, '38 km/l', 'mt15.jpg', 'Street bike with sporty handling', 4.3, 'Available'),
('R15', 'Yamaha', 'R15', 2, 'KA02AB0004', 'Petrol', 'Manual', 2, 100, 600, '36 km/l', 'r15.jpg', 'High-performance sports bike', 4.6, 'Available'),
('Duke 200', 'KTM', 'Duke 200', 2, 'KA02AB0005', 'Petrol', 'Manual', 2, 95, 580, '35 km/l', 'duke200.jpg', 'Aggressive street bike for thrill seekers', 4.5, 'Available'),
('SP 125', 'Honda', 'SP 125', 2, 'KA02AB0006', 'Petrol', 'Manual', 2, 75, 480, '45 km/l', 'sp125.jpg', 'Reliable 125cc commuter bike', 4.2, 'Available');

-- Insert vehicle data - SCOOTERS
INSERT INTO vehicles (name, brand, model, category_id, registration_number, fuel_type, transmission, seating_capacity, price_per_hour, price_per_day, mileage, image, description, rating, availability_status) VALUES
('Activa', 'Honda', 'Activa 6G', 3, 'KA03AB0001', 'Petrol', 'Automatic', 2, 60, 350, '50 km/l', 'activa.jpg', 'Best-selling automatic scooter', 4.7, 'Available'),
('Jupiter', 'TVS', 'Jupiter', 3, 'KA03AB0002', 'Petrol', 'Automatic', 2, 65, 380, '48 km/l', 'jupiter.jpg', 'Fuel-efficient family scooter', 4.4, 'Available'),
('Access', 'Suzuki', 'Access 125', 3, 'KA03AB0003', 'Petrol', 'Automatic', 2, 70, 400, '48 km/l', 'access.jpg', 'Premium scooter with advanced features', 4.5, 'Available'),
('RayZR', 'Yamaha', 'RayZR 125', 3, 'KA03AB0004', 'Petrol', 'Automatic', 2, 68, 390, '49 km/l', 'rayzr.jpg', 'Stylish commuter scooter with great mileage', 4.3, 'Available');

-- Create index for faster queries
CREATE INDEX idx_vehicle_name ON vehicles(name);
CREATE INDEX idx_vehicle_brand ON vehicles(brand);
CREATE INDEX idx_user_email ON users(email);
