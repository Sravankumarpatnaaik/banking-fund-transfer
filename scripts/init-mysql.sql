-- Local development database. The application creates the tables on startup.
CREATE DATABASE IF NOT EXISTS meridian_bank CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS meridian_bank_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'bankuser'@'localhost' IDENTIFIED BY 'meridian_local';
CREATE USER IF NOT EXISTS 'bankuser'@'127.0.0.1' IDENTIFIED BY 'meridian_local';
GRANT ALL PRIVILEGES ON meridian_bank.* TO 'bankuser'@'localhost';
GRANT ALL PRIVILEGES ON meridian_bank.* TO 'bankuser'@'127.0.0.1';
GRANT ALL PRIVILEGES ON meridian_bank_test.* TO 'bankuser'@'localhost';
GRANT ALL PRIVILEGES ON meridian_bank_test.* TO 'bankuser'@'127.0.0.1';
FLUSH PRIVILEGES;
