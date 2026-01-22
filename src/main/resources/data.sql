INSERT INTO tb_member (user_id, name) VALUES ('kdr001', '키다리사원1');
INSERT INTO tb_member (user_id, name) VALUES ('kdr002', '키다리사원2');
INSERT INTO tb_member (user_id, name) VALUES ('kdr003', '키다리사원3');


-- 1분기 이벤트
INSERT INTO tb_events (title, start_at, end_at, draw_at, total_coin_limit, issued_coin_count, user_coin_limit, status)
VALUES ('2026 1분기 휴가쿠폰 이벤트', '2026-01-01 00:00:00', '2026-03-31 23:59:59', '2026-04-02 23:59:59', 900, 0, 3, 'ACTIVE');

-- 2분기 이벤트
INSERT INTO tb_events (title, start_at, end_at, draw_at, total_coin_limit, issued_coin_count, user_coin_limit, status)
VALUES ('2026 2분기 휴가쿠폰 이벤트', '2026-04-01 00:00:00', '2026-06-30 23:59:59','2026-04-02 23:59:59', 900, 0, 3, 'READY');

-- 3분기 이벤트
INSERT INTO tb_events (title, start_at, end_at, draw_at, total_coin_limit, issued_coin_count, user_coin_limit, status)
VALUES ('2026 3분기 휴가쿠폰 이벤트', '2026-07-01 00:00:00', '2026-09-30 23:59:59','2026-04-02 23:59:59', 900, 0, 3, 'READY');

-- 4분기 이벤트
INSERT INTO tb_events (title, start_at, end_at, draw_at, total_coin_limit, issued_coin_count, user_coin_limit, status)
VALUES ('2026 4분기 휴가쿠폰 이벤트', '2026-10-01 00:00:00', '2026-12-31 23:59:59','2026-04-02 23:59:59', 900, 0, 3, 'READY');



-- 1분기 휴가1일권 쿠폰
INSERT INTO tb_coupons (event_id, coupon_type, coupon_group_code, coupon_name, issue_limit, issued_count, status)
VALUES (1, 'EVENT', 'VAC001','2026 1분기 1일 휴가권', 3, 0, 'AVAILABLE');

-- 1분기 휴가3일권 쿠폰
INSERT INTO tb_coupons (event_id, coupon_type, coupon_group_code, coupon_name, issue_limit, issued_count, status)
VALUES (1, 'EVENT', 'VAC003','2026 1분기 3일 휴가권', 3, 0, 'AVAILABLE');


-- 2분기 휴가1일권 쿠폰
INSERT INTO tb_coupons (event_id, coupon_type, coupon_group_code, coupon_name, issue_limit, issued_count, status)
VALUES (2, 'EVENT', 'VAC001','2026 2분기 1일 휴가권', 3, 0, 'CLOSED');

-- 2분기 휴가3일권 쿠폰
INSERT INTO tb_coupons (event_id, coupon_type, coupon_group_code, coupon_name, issue_limit, issued_count, status)
VALUES (2, 'EVENT', 'VAC003','2026 2분기 3일 휴가권', 3, 0, 'CLOSED');


-- 3분기 휴가1일권 쿠폰
INSERT INTO tb_coupons (event_id, coupon_type, coupon_group_code, coupon_name, issue_limit, issued_count, status)
VALUES (3, 'EVENT', 'VAC001','2026 3분기 1일 휴가권', 3, 0, 'CLOSED');

-- 3분기 휴가3일권 쿠폰
INSERT INTO tb_coupons (event_id, coupon_type, coupon_group_code, coupon_name, issue_limit, issued_count, status)
VALUES (3, 'EVENT', 'VAC003','2026 3분기 3일 휴가권', 3, 0, 'CLOSED');


-- 4분기 휴가1일권 쿠폰
INSERT INTO tb_coupons (event_id, coupon_type, coupon_group_code, coupon_name, issue_limit, issued_count, status)
VALUES (4, 'EVENT', 'VAC001','2026 4분기 1일 휴가권', 3, 0, 'CLOSED');

-- 4분기 휴가3일권 쿠폰
INSERT INTO tb_coupons (event_id, coupon_type, coupon_group_code, coupon_name, issue_limit, issued_count, status)
VALUES (4, 'EVENT', 'VAC003','2026 4분기 3일 휴가권', 3, 0, 'CLOSED');
