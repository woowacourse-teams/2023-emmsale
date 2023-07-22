drop table if exists kerdy.career;
drop table if exists kerdy.member;
drop table if exists kerdy.member_career;

create table kerdy.career
(
    id       bigint auto_increment primary key,
    activity varchar(255) not null,
    name     varchar(255) not null
);

create table kerdy.member
(
    id         bigint auto_increment primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    github_id  bigint       not null unique,
    name       varchar(255) not null
);

create table kerdy.member_career
(
    id         bigint auto_increment primary key,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    career_id  bigint not null,
    member_id  bigint not null
);
