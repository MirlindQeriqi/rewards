CREATE TABLE `transaction` (
     `id` SERIAL PRIMARY KEY,
     `customer_id` BIGINT(20) NOT NULL,
     `date` TIMESTAMP NOT NULL,
     `amount` DECIMAL(12,2) NOT NULL
)ENGINE = INNODB
 DEFAULT CHARSET = utf8mb4;