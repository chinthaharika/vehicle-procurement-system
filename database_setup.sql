-- =================================================================================
-- PROJECT: Smart Vehicle Procurement
-- DESCRIPTION: Database schema creation script for MySQL.
-- PURPOSE: This script creates the database and initializes tables with
--          professional data types, constraints, and relationships.
-- =================================================================================

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS `procurement_blockchain`;
USE `procurement_blockchain`;

-- ==========================================
-- TABLE: users
-- ==========================================
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `login_id` VARCHAR(255) NOT NULL UNIQUE,
    `mobile` VARCHAR(10),
    `name` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` ENUM('BUYER', 'SELLER', 'ADMIN'),
    `status` ENUM('WAITING', 'APPROVED', 'REJECTED') DEFAULT 'WAITING'
);

-- ==========================================
-- TABLE: vehicles
-- ==========================================
CREATE TABLE IF NOT EXISTS `vehicles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `accidents_history` VARCHAR(2000),
    `block_hash` VARCHAR(255),
    `created_at` DATETIME(6),
    `document` MEDIUMBLOB,
    `document_content_type` VARCHAR(255),
    `document_filename` VARCHAR(255),
    `image` MEDIUMBLOB,
    `image_content_type` VARCHAR(255),
    `price` DECIMAL(38,2) NOT NULL,
    `status` ENUM('AVAILABLE', 'PENDING', 'SOLD') DEFAULT 'AVAILABLE',
    `vehicle_number` VARCHAR(255) NOT NULL UNIQUE,
    `buyer_id` BIGINT,
    `seller_id` BIGINT,
    CONSTRAINT `fk_vehicle_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_vehicle_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`)
);

-- ==========================================
-- TABLE: transfer_history
-- ==========================================
CREATE TABLE IF NOT EXISTS `transfer_history` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `previous_hash` VARCHAR(255),
    `price` DECIMAL(38,2),
    `transaction_hash` VARCHAR(255),
    `transfer_time` DATETIME(6),
    `from_user_id` BIGINT,
    `to_user_id` BIGINT NOT NULL,
    `vehicle_id` BIGINT NOT NULL,
    CONSTRAINT `fk_transfer_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_transfer_to_user` FOREIGN KEY (`to_user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_transfer_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`)
);

-- ==========================================
-- TABLE: feedback
-- ==========================================
CREATE TABLE IF NOT EXISTS `feedback` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `comment` VARCHAR(1000),
    `created_at` DATETIME(6),
    `rating` INT NOT NULL,
    `buyer_id` BIGINT NOT NULL,
    `vehicle_id` BIGINT NOT NULL,
    CONSTRAINT `fk_feedback_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_feedback_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`)
);

-- =================================================================================
-- INITIALIZATION COMPLETE
-- =================================================================================