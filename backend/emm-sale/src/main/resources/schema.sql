drop table if exists kerdy.activity;
drop table if exists kerdy.event;
drop table if exists kerdy.member;
drop table if exists kerdy.comment;
drop table if exists kerdy.member_activity;
drop table if exists kerdy.tag;
drop table if exists kerdy.event_tag;
drop table if exists kerdy.member_tag;
drop table if exists kerdy.event_member;
drop table if exists kerdy.request_notification;
drop table if exists kerdy.fcm_token;
drop table if exists kerdy.block;
drop table if exists kerdy.update_notification;
drop table if exists kerdy.report;
drop table if exists kerdy.scrap;
drop table if exists kerdy.message;
drop table if exists kerdy.room;
drop table if exists kerdy.feed;
drop table if exists kerdy.image;
drop table if exists kerdy.notification;

create table activity
(
    id   bigint auto_increment primary key,
    type varchar(255) not null,
    name varchar(255) not null
);

create table event
(
    id              bigint auto_increment primary key,
    created_at      datetime(6),
    updated_at      datetime(6),
    end_date        datetime(6) not null,
    information_url varchar(255) not null,
    location        varchar(255) not null,
    name            varchar(255) not null,
    start_date      datetime(6) not null,
    image_url       varchar(255),
    type            varchar(20)  not null
);

create table member
(
    id               bigint auto_increment primary key,
    created_at       datetime(6),
    updated_at       datetime(6),
    description      varchar(255) not null default '',
    github_id        bigint       not null unique,
    image_url        varchar(255) not null,
    name             varchar(255),
    open_profile_url varchar(255) null
);

create table comment
(
    id         bigint auto_increment primary key,
    created_at datetime(6),
    updated_at datetime(6),
    content    varchar(255) not null,
    is_deleted bit          not null,
    event_id   bigint       not null,
    member_id  bigint       not null,
    parent_id  bigint null
);

create table member_activity
(
    id          bigint auto_increment primary key,
    created_at  datetime(6),
    updated_at  datetime(6),
    activity_id bigint not null,
    member_id   bigint not null
);

create table tag
(
    id   bigint auto_increment primary key,
    name varchar(255) not null
);

create table event_tag
(
    id       bigint auto_increment primary key,
    event_id bigint not null,
    tag_id   bigint not null
);

create table member_tag
(
    id         bigint auto_increment primary key,
    created_at datetime(6),
    updated_at datetime(6),
    member_id  bigint not null,
    tag_id     bigint not null
);

create table event_member
(
    id        bigint auto_increment primary key,
    member_id bigint not null,
    event_id  bigint not null
);
create table notification
(
    id          bigint auto_increment primary key,
    created_at  datetime(6),
    updated_at  datetime(6),
    event_id    bigint       not null,
    message     varchar(255) not null,
    receiver_id bigint       not null,
    sender_id   bigint       not null,
    status      varchar(255) not null
);

create table fcm_token
(
    id        bigint auto_increment primary key,
    token     varchar(255) not null,
    member_id bigint       not null unique
);

-- 2023-08-08 14:40
alter table event_member
    add column content varchar(255) not null;
alter table event_member
    add column created_at datetime(6);
alter table event_member
    add column updated_at datetime(6);

-- 2023.08.08 17:04
rename
table notification TO request_notification;

create table update_notification
(
    id          bigint auto_increment primary key,
    receiver_id bigint       not null,
    redirect_id bigint       not null,
    type        varchar(255) not null,
    created_at  datetime(6)
);

-- 2023-08-08 17:20
create table block
(
    id                bigint auto_increment primary key,
    block_member_id   bigint not null,
    request_member_id bigint not null,
    created_at        datetime(6) null,
    updated_at        datetime(6) null
);

-- 2023-08-08 23:00
alter table event
    add column apply_start_date datetime(6) not null;
alter table event
    add column apply_end_date datetime(6) not null;

-- 2023-08-10 12:50
create table scrap
(
    id         bigint auto_increment primary key,
    member_id  bigint not null,
    event_id   bigint not null,
    created_at datetime(6),
    updated_at datetime(6)
);

-- 2023-08-11 21:41
alter table request_notification
    add column is_read bit not null;

-- 2023-08-12 12:55
alter table update_notification
    add column is_read bit not null;

-- 2023-08-14 13:10
create table report
(
    id          bigint auto_increment primary key,
    reporter_id bigint      not null,
    reported_id bigint      not null,
    type        varchar(20) not null,
    content_id  bigint      not null,
    created_at  datetime(6),
    updated_at  datetime(6)
);

-- 2023-08-16 13:42

alter table member
    add column github_username varchar(40) not null default '';

-- 2023-08-31 19:57

create table room
(
    uuid             varchar(40) not null,
    member_id        bigint      not null,
    last_exited_time datetime(6),
    primary key (uuid, member_id)
);

create table message
(
    id         bigint       not null auto_increment,
    content    varchar(255) not null,
    created_at datetime(6),
    sender_id  bigint       not null,
    room_id    varchar(40)  not null,
    primary key (id)
);

-- 2023-09-01 23:06
create table feed
(
    id         bigint auto_increment primary key,
    writer_id  bigint        not null,
    title      varchar(50)   not null,
    content    varchar(1000) not null,
    event_id   bigint        not null,
    is_deleted bit           not null,
    created_at datetime(6) null,
    updated_at datetime(6) null
);

alter table comment rename column event_id to feed_id;

-- 2023-09-14 13:39

create table image
(
    id           bigint auto_increment primary key,
    name         varchar(50) not null,
    type         varchar(20) not null,
    content_id   bigint      not null,
    order_number int         not null,
    created_at   datetime(6)
);

-- 2023-09-14 16:43
alter table event
    add column payment_type varchar(50) not null;
alter table event
    add column event_mode varchar(50) not null;

-- 2023-09-20 20:25
alter table event
    add column organization varchar(50) not null;

-- 2023-09-27 10:54
create table notification
(
    id        bigint auto_increment primary key,
    type      varchar(20) not null,
    json_data mediumtext  not null,
    is_read   bit         not null
);

-- 2023-09-29 18:33
alter table event drop image_url;
