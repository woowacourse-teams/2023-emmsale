drop table if exists kerdy.activity;
drop table if exists kerdy.event;
drop table if exists kerdy.member;
drop table if exists kerdy.comment;
drop table if exists kerdy.member_activity;
drop table if exists kerdy.tag;
drop table if exists kerdy.event_tag;
drop table if exists kerdy.member_tag;
drop table if exists kerdy.event_member;

create table activity
(
    id   bigint auto_increment primary key,
    type varchar(255) not null,
    name varchar(255) not null
);

create table event
(
    id              bigint auto_increment primary key,
    created_at      datetime(6)  not null,
    updated_at      datetime(6)  not null,
    end_date        datetime(6)  not null,
    information_url varchar(255) not null,
    location        varchar(255) not null,
    name            varchar(255) not null,
    start_date      datetime(6)  not null
);

create table member
(
    id               bigint auto_increment primary key,
    created_at       datetime(6)  not null,
    updated_at       datetime(6)  not null,
    description      varchar(255) null,
    github_id        bigint       not null unique,
    image_url        varchar(255) not null,
    name             varchar(255) not null,
    open_profile_url varchar(255) null
);

create table comment
(
    id         bigint auto_increment primary key,
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null,
    content    varchar(255) not null,
    is_deleted bit          not null,
    event_id   bigint       not null,
    member_id  bigint       not null,
    parent_id  bigint       null
);

create table member_activity
(
    id          bigint auto_increment primary key,
    created_at  datetime(6) not null,
    updated_at  datetime(6) not null,
    activity_id bigint      not null,
    member_id   bigint      not null
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
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    member_id  bigint      not null,
    tag_id     bigint      not null
);

create table event_member
(
    id        bigint auto_increment primary key,
    member_id bigint not null,
    event_id  bigint not null
);
