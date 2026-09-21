-- Tạo database pythonmaster cho MySQL
CREATE DATABASE IF NOT EXISTS pythonmaster 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE pythonmaster;

-- Note: Các bảng sẽ được Hibernate tự động tạo khi ứng dụng khởi động
-- với ddl-auto=update
-- Không cần tạo thủ công các bảng

-- Tạo user nếu cần (optional)
-- CREATE USER IF NOT EXISTS 'pythonmaster_user'@'localhost' IDENTIFIED BY 'your_password';
-- GRANT ALL PRIVILEGES ON pythonmaster.* TO 'pythonmaster_user'@'localhost';
-- FLUSH PRIVILEGES;
