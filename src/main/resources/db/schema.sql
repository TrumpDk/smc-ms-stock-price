CREATE TABLE IF NOT EXISTS `t_stock_price` (
`id` INTEGER  PRIMARY KEY AUTO_INCREMENT,
`stock_exchange` VARCHAR(50) NOT NULL,
`company_stock_code` VARCHAR(50) NOT NULL,
`current_price` DECIMAL,
`date_time` TIMESTAMP
);