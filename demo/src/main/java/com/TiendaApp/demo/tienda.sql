create schema tienda_db;
use tienda_db;

create table roles (
    id int primary key not null auto_increment,
    name varchar(50) not null  
);

create table user(
    id int primary key not null auto_increment,
    username varchar(50) not null unique,
    email varchar(225) not null unique,
    password varchar(255) not null,
    active boolean not null default true,
    rol_id int not null,
    foreing key (rol_id) references roles (id)
);

create table categories(
    id int primary key not null auto_increment,
    name varchar (100) not null unique 
);

create table products(
    id int primary key not null auto_increment,
    name varchar(150) not null,
    description varchar(500),
    price decimal(12,2) not null,
    stock int not null,
    category_id int not null,
    seller_id int not null,
    foreign key (category_id) references categories (id),
    foreign key (seller_id) references users (id)
);

create table orders(
    id int primary key not null auto_increment,
  client_id int not null,
  product_id int not null,
  quantity int not null,
  total decimal(12,2) not null,
  status varchar(20) not null default 'PENDIENTE',
  created_at datetime not null default current_timestamp,
  foreign key (client_id) references users (id),
  foreign key (product_id) references products (id)
);

insert into roles (name) values ('ADMIN'), ('CLIENTE'), ('VENDEDOR');

insert into user (username, email, password, active, rol_id)
values ('admin', 'admin@tienda.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 1);