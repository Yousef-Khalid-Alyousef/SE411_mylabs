drop table if exists product;

CREATE table product (
 id int not null auto_increment PRIMARY KEY,
 name varchar(100) not null,
 description varchar(100),
 price DECIMAL(8, 2)
);

DROP TABLE IF EXISTS app_user;
CREATE TABLE app_user (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username varchar(100) NOT NULL UNIQUE,
    password varchar(100) NOT NULL
);
