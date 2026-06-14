CREATE DATABASE IF NOT EXISTS campus_express CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_express;

CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `student_id` VARCHAR(20) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(11) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
  `status` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `express` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `tracking_number` VARCHAR(50) NOT NULL,
  `company` VARCHAR(50) NOT NULL,
  `recipient_name` VARCHAR(50) NOT NULL,
  `recipient_phone` VARCHAR(11) NOT NULL,
  `location` VARCHAR(50) NOT NULL,
  `pickup_code` VARCHAR(10) NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  `admin_id` BIGINT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tracking_number` (`tracking_number`),
  UNIQUE KEY `uk_pickup_code` (`pickup_code`),
  KEY `idx_recipient_phone` (`recipient_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `express_id` BIGINT NOT NULL,
  `reservation_date` DATE NOT NULL,
  `start_time` TIME NOT NULL,
  `end_time` TIME NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_express_id` (`express_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `is_read` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `admin` (`username`, `password`, `role`) VALUES ('admin', '5f4dcc3b5aa765d61d8327deb882cf99', 'ADMIN');

INSERT INTO `user` (`student_id`, `name`, `phone`, `password`, `role`) VALUES 
('2021001001', 'ZhangSan', '13800138001', '5f4dcc3b5aa765d61d8327deb882cf99', 'STUDENT'),
('2021001002', 'LiSi', '13800138002', '5f4dcc3b5aa765d61d8327deb882cf99', 'STUDENT'),
('2021001003', 'WangWu', '13800138003', '5f4dcc3b5aa765d61d8327deb882cf99', 'STUDENT');

INSERT INTO `express` (`tracking_number`, `company`, `recipient_name`, `recipient_phone`, `location`, `pickup_code`, `status`, `admin_id`) VALUES 
('SF1234567890', 'SF', 'ZhangSan', '13800138001', 'A-0123', '240101', 'PENDING', 1),
('YT9876543210', 'YT', 'LiSi', '13800138002', 'A-0456', '240102', 'PENDING', 1),
('ZTO5678901234', 'ZTO', 'WangWu', '13800138003', 'B-0789', '240103', 'PENDING', 1);

CREATE TABLE IF NOT EXISTS `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1,
  `admin_id` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
