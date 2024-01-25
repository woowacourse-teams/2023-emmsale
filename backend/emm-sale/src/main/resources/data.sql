truncate table activity;
truncate table event;
truncate table member;
truncate table comment;
truncate table member_activity;
truncate table tag;
truncate table event_tag;
truncate table member_tag;
truncate table event_member;
truncate table fcm_token;
truncate table block;
truncate table report;
truncate table scrap;
truncate table message;
truncate table room;
truncate table feed;
truncate table image;

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

insert into member(id, name, image_url, open_profile_url, description, oauth_id, github_username,
                   created_at,
                   updated_at)
values (1, 'member1', 'https://imageurl.com', 'https://openprofileurl.com', '안녕하세요.', 1,
        'amaran-th', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member(id, name, image_url, open_profile_url, description, oauth_id, github_username,
                   created_at,
                   updated_at)
values (2, 'member2', 'https://imageurl.com', 'https://openprofileurl.com', '반갑습니다.', 2,
        'amaran-th22', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member(id, name, image_url, open_profile_url, description, oauth_id, github_username,
                   created_at,
                   updated_at)
values (3, 'admin', 'https://imageurl.com', 'https://openprofileurl.com', '반갑습니다.', 3,
        'amaran-th22', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (1, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (2, 2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (3, 3, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into member_activity(id, activity_id, member_id, created_at, updated_at)
values (4, 1, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

insert into tag(id, name)
values (1, '백엔드');
insert into tag(id, name)
values (2, '프론트엔드');
insert into tag(id, name)
values (3, '안드로이드');
insert into tag(id, name)
values (4, 'IOS');
insert into tag(id, name)
values (5, 'AI');
insert into tag(id, name)
values (6, '알고리즘');
insert into tag(id, name)
values (7, '정보보안');

insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (1, '인프콘 2023', '2023-06-01T12:00:00', '2023-09-01T12:00:00', '2023-05-01T12:00:00',
        '2023-06-01T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'CONFERENCE', 'OFFLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at, type, event_mode, payment_type,
                  organization)
values (2, 'AI 컨퍼런스', '2023-07-22T12:00:00', '2023-07-30T12:00:00', '2023-07-01T12:00:00',
        '2023-07-22T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'CONFERENCE', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (3, '모바일 컨퍼런스', '2023-08-03T12:00:00', '2023-09-03T12:00:00', '2023-08-01T12:00:00',
        '2023-08-02T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'CONFERENCE', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (4, '안드로이드 컨퍼런스', '2023-06-29T12:00:00', '2023-07-16T12:00:00', '2023-06-01T12:00:00',
        '2023-06-20T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'CONFERENCE', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (5, '웹 컨퍼런스', '2023-07-03T12:00:00', '2023-08-03T12:00:00', '2023-07-03T12:00:00',
        '2023-08-03T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'CONFERENCE', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (6, '옛날 웹 컨퍼런스', '2022-07-03T12:00:00', '2022-08-03T12:00:00', '2022-07-03T12:00:00',
        '2022-08-03T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'CONFERENCE', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (7, '알고리즘 경진대회', '2023-06-01T12:00:00', '2023-09-01T12:00:00', '2023-06-01T12:00:00',
        '2023-09-01T12:00:00', '온라인', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'COMPETITION', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (8, '첨단 해커톤', '2023-07-22T12:00:00', '2023-07-30T12:00:00', '2023-07-22T12:00:00',
        '2023-07-30T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'COMPETITION', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (9, 'AI 아이디어 공모전', '2023-08-03T12:00:00', '2023-09-03T12:00:00', '2023-08-03T12:00:00',
        '2023-09-03T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'COMPETITION', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (10, '구름톤', '2023-06-29T12:00:00', '2023-07-16T12:00:00', '2023-06-29T12:00:00',
        '2023-07-16T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'COMPETITION', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location, information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (11, 'CTF', '2023-07-03T12:00:00', '2023-08-03T12:00:00', '2023-07-03T12:00:00',
        '2023-08-03T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'COMPETITION', 'ONLINE', 'PAID', '행사기관');
insert into event(id, name, start_date, end_date, apply_start_date, apply_end_date,
                  location,
                  information_url, created_at, updated_at,
                  type, event_mode, payment_type, organization)
values (12, '보안 경진대회', '2022-07-03T12:00:00', '2022-08-03T12:00:00', '2022-07-03T12:00:00',
        '2022-08-03T12:00:00', '코엑스', 'https://www.naver.com',
        CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'COMPETITION', 'ONLINE', 'PAID', '행사기관');


insert into event_tag(id, event_id, tag_id)
values (1, 1, 1);
insert into event_tag(id, event_id, tag_id)
values (2, 1, 2);
insert into event_tag(id, event_id, tag_id)
values (3, 1, 3);
insert into event_tag(id, event_id, tag_id)
values (4, 1, 4);
insert into event_tag(id, event_id, tag_id)
values (5, 1, 5);
insert into event_tag(id, event_id, tag_id)
values (6, 2, 5);
insert into event_tag(id, event_id, tag_id)
values (7, 3, 3);
insert into event_tag(id, event_id, tag_id)
values (8, 3, 4);
insert into event_tag(id, event_id, tag_id)
values (9, 4, 3);
insert into event_tag(id, event_id, tag_id)
values (10, 5, 1);
insert into event_tag(id, event_id, tag_id)
values (11, 5, 2);
insert into event_tag(id, event_id, tag_id)
values (12, 7, 6);
insert into event_tag(id, event_id, tag_id)
values (13, 8, 1);
insert into event_tag(id, event_id, tag_id)
values (14, 8, 2);
insert into event_tag(id, event_id, tag_id)
values (15, 8, 3);
insert into event_tag(id, event_id, tag_id)
values (16, 9, 5);
insert into event_tag(id, event_id, tag_id)
values (17, 10, 1);
insert into event_tag(id, event_id, tag_id)
values (18, 10, 2);
insert into event_tag(id, event_id, tag_id)
values (19, 10, 3);
insert into event_tag(id, event_id, tag_id)
values (20, 11, 7);
insert into event_tag(id, event_id, tag_id)
values (21, 12, 7);

insert into event_member (member_id, event_id, content) value (1, 1, '빈 게시글 내용');
insert into event_member (member_id, event_id, content) value (2, 1, '빈 게시글 내용');
insert into event_member (member_id, event_id, content) value (3, 1, '빈 게시글 내용');

insert into event_member (member_id, event_id, content) value (1, 2, '빈 게시글 내용');
insert into event_member (member_id, event_id, content) value (2, 2, '빈 게시글 내용');
insert into event_member (member_id, event_id, content) value (3, 2, '빈 게시글 내용');
