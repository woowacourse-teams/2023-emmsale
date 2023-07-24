drop table if exists kerdy.activity;
drop table if exists kerdy.member;
drop table if exists kerdy.member_activity;

create table kerdy.activity
(
    id   bigint auto_increment primary key,
    type varchar(255) not null,
    name varchar(255) not null
);

create table kerdy.member
(
    id         bigint auto_increment primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    github_id  bigint       not null unique,
    name       varchar(255) not null
);

create table kerdy.member_activity
(
    id          bigint auto_increment primary key,
    created_at  datetime(6) not null,
    updated_at  datetime(6) not null,
    activity_id bigint not null,
    member_id   bigint not null
);
