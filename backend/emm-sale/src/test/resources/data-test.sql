truncate table activity;
truncate table event;
truncate table member;
truncate table comment;
truncate table member_activity;
truncate table tag;
truncate table event_tag;
truncate table member_tag;
truncate table event_member;
truncate table request_notification;
truncate table update_notification;
truncate table block;
truncate table fcm_token;
truncate table report;

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

insert into member(id, name, image_url, open_profile_url, github_id, created_at, updated_at)
values (1, null, 'https://imageurl.com', 'https://openprofileurl.com', 1, CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP());

insert into member(id, name, image_url, open_profile_url, github_id, created_at, updated_at)
values (2, 'member2', 'https://imageurl.com', 'https://openprofileurl.com', 2, CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (1, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (2, 2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (3, 3, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (4, 1, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
