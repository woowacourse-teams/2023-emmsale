truncate table kerdy.member_activity;
truncate table kerdy.activity;
truncate table kerdy.member;

insert into activity(id, type, name)
values (1, 'CLUB', 'YAPP');

insert into activity(id, type, name)
values (2, 'CLUB', 'DND');

insert into activity(id, type, name)
values (3, 'CLUB', 'nexters');

insert into activity(id, type, name)
values (4, 'CONFERENCE', '인프콘');

insert into activity(id, type, name)
values (5, 'EDUCATION', '우아한테크코스');

insert into activity(id, type, name)
values (6, 'JOB', 'Backend');

insert into member(id, name, github_id, created_at, updated_at)
values (1, 'member1', 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member(id, name, github_id, created_at, updated_at)
values (2, 'member2', 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (1, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (2, 2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (3, 3, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (4, 1, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
