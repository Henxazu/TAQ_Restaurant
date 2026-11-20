CREATE DATABASE  IF NOT EXISTS `restaurant_management` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `restaurant_management`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: restaurant_management
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Appetizers','Starters, salads, and light bites',1,'2026-11-20 16:01:30'),(2,'Main Course','Signature grilled dishes, steaks, and pastas',1,'2026-11-20 16:01:30'),(3,'Beverages','Fresh juices, coffee, and soft drinks',1,'2026-11-20 16:01:30'),(4,'Desserts','Cakes, puddings, and ice cream',1,'2026-11-20 16:01:30');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dining_tables`
--

DROP TABLE IF EXISTS `dining_tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dining_tables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `table_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacity` int NOT NULL,
  `zone` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Main Hall',
  `status` enum('AVAILABLE','RESERVED','OCCUPIED','MAINTENANCE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'AVAILABLE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `table_code` (`table_code`),
  KEY `idx_tables_status_capacity` (`status`,`capacity`),
  CONSTRAINT `dining_tables_chk_1` CHECK ((`capacity` >= 1))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dining_tables`
--

LOCK TABLES `dining_tables` WRITE;
/*!40000 ALTER TABLE `dining_tables` DISABLE KEYS */;
INSERT INTO `dining_tables` VALUES (1,'T-01',2,'Window','AVAILABLE'),(2,'T-02',2,'Window','AVAILABLE'),(3,'T-03',4,'Main Hall','AVAILABLE'),(4,'T-04',4,'Main Hall','AVAILABLE'),(5,'T-05',6,'Main Hall','AVAILABLE'),(6,'T-06',8,'VIP Room','AVAILABLE');
/*!40000 ALTER TABLE `dining_tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu_items`
--

DROP TABLE IF EXISTS `menu_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `price` decimal(12,2) NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT '1',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_menu_category_avail` (`category_id`,`is_available`,`is_deleted`),
  CONSTRAINT `fk_menu_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `menu_items_chk_1` CHECK ((`price` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_items`
--

LOCK TABLES `menu_items` WRITE;
/*!40000 ALTER TABLE `menu_items` DISABLE KEYS */;
INSERT INTO `menu_items` VALUES (1,1,'Crispy Calamari','Lightly battered squid rings with tartar sauce',125000.00,NULL,1,0,'2026-11-20 16:01:30','2026-11-20 16:01:30'),(2,1,'Caesar Salad','Romaine lettuce, parmesan, croutons, and classic dressing',95000.00,NULL,1,0,'2026-11-20 16:01:30','2026-11-20 16:01:30'),(3,2,'Grilled Ribeye Steak','250g Australian beef ribeye with black pepper sauce',385000.00,NULL,1,0,'2026-11-20 16:01:30','2026-11-20 16:01:30'),(4,2,'Pan-Seared Salmon','Served with asparagus and lemon butter sauce',310000.00,NULL,1,0,'2026-11-20 16:01:30','2026-11-20 16:01:30'),(5,2,'Spaghetti Carbonara','Creamy pasta with smoked bacon and parmesan',165000.00,NULL,1,0,'2026-11-20 16:01:30','2026-11-20 16:01:30'),(6,3,'Fresh Passion Fruit Juice','Chilled passion fruit juice with mint',45000.00,NULL,1,0,'2026-11-20 16:01:30','2026-11-20 16:01:30'),(7,3,'Iced Espresso Latte','Double shot espresso over fresh milk',55000.00,NULL,1,0,'2026-11-20 16:01:30','2026-11-20 16:01:30'),(8,4,'Classic Tiramisu','Coffee-soaked ladyfingers with mascarpone cream',85000.00,NULL,1,0,'2026-11-20 16:01:30','2026-11-20 16:01:30');
/*!40000 ALTER TABLE `menu_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `menu_item_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  `status` enum('PENDING','COOKING','SERVED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_item_menu` (`menu_item_id`),
  KEY `idx_items_order_status` (`order_id`,`status`),
  CONSTRAINT `fk_item_menu` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`),
  CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_items_chk_1` CHECK ((`quantity` between 1 and 20)),
  CONSTRAINT `order_items_chk_2` CHECK ((`unit_price` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `table_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `status` enum('ORD_ACTIVE','PAYMENT_REQUESTED','PAID','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ORD_ACTIVE',
  `payment_method` enum('CASH','BANK_TRANSFER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
  `paid_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_ord_reservation` (`reservation_id`),
  KEY `fk_ord_customer` (`customer_id`),
  KEY `fk_ord_staff` (`staff_id`),
  KEY `idx_orders_status_paid` (`status`,`paid_at`),
  KEY `idx_orders_table` (`table_id`,`status`),
  CONSTRAINT `fk_ord_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_ord_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`),
  CONSTRAINT `fk_ord_staff` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_ord_table` FOREIGN KEY (`table_id`) REFERENCES `dining_tables` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint NOT NULL,
  `table_id` bigint DEFAULT NULL,
  `reservation_date` date NOT NULL,
  `time_slot` time NOT NULL,
  `guest_count` int NOT NULL,
  `special_notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('RES_PENDING','RES_CONFIRMED','RES_REJECTED','RES_CANCELLED','RES_CHECKED_IN','RES_NO_SHOW','COMPLETED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'RES_PENDING',
  `reject_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_res_table` (`table_id`),
  KEY `idx_res_date_status` (`reservation_date`,`status`),
  KEY `idx_res_customer` (`customer_id`),
  CONSTRAINT `fk_res_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_res_table` FOREIGN KEY (`table_id`) REFERENCES `dining_tables` (`id`),
  CONSTRAINT `reservations_chk_1` CHECK ((`guest_count` between 1 and 20))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ROLE_CUSTOMER','ROLE_STAFF','ROLE_ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ROLE_CUSTOMER',
  `status` enum('ACTIVE','LOCKED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_users_role_status` (`role`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Restaurant Admin','admin@restaurant.local','0901000001','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','ROLE_ADMIN','ACTIVE','2026-11-20 16:01:30','2026-11-20 16:01:30'),(2,'Staff Waiter 01','staff01@restaurant.local','0901000002','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','ROLE_STAFF','ACTIVE','2026-11-20 16:01:30','2026-11-20 16:01:30'),(3,'Sample Customer','customer01@restaurant.local','0901000003','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','ROLE_CUSTOMER','ACTIVE','2026-11-20 16:01:30','2026-11-20 16:01:30');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-20 23:03:03
