drop table if exists kerdy.member_activity;
drop table if exists kerdy.activity;
drop table if exists kerdy.member;

create table kerdy.activity
(
    id   bigint auto_increment primary key,
    type varchar(255) not null,
    name varchar(255) not null
);

create table kerdy.member
(
    id               bigint auto_increment primary key,
    created_at       datetime(6) not null,
    updated_at       datetime(6) not null,
    description      varchar(255) null,
    github_id        bigint       not null unique,
    image_url        varchar(255) not null,
    name             varchar(255) not null,
    open_profile_url varchar(255) null
);

create table kerdy.member_activity
(
    id          bigint auto_increment primary key,
    created_at  datetime(6) not null,
    updated_at  datetime(6) not null,
    activity_id bigint not null,
    member_id   bigint not null
);
