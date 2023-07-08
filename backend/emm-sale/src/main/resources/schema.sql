create table member
(
    id         bigint auto_increment primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null
);

create table price_history
(
    id         bigint auto_increment primary key,
    created_at datetime(6) not null,
    price      decimal(19, 2) not null,
    product_id bigint         not null,
);

create table product
(
    id                   bigint auto_increment primary key,
    created_at           datetime(6) not null,
    updated_at           datetime(6) not null,
    brand_name           varchar(255)   not null,
    image_url            varchar(255)   not null,
    name                 varchar(255)   not null,
    current_price        decimal(19, 2) not null,
    original_price       decimal(19, 2) not null,
    purchase_temperature double         not null,
    product_url          varchar(255)   not null
);

create table wish
(
    id         bigint auto_increment primary key,
    created_at datetime(6) not null,
    member_id  bigint not null,
    product_id bigint not null,
);

