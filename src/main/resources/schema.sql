DROP TABLE IF EXISTS tb_member;
DROP TABLE IF EXISTS tb_events;
DROP TABLE IF EXISTS tb_coin_issuances;
DROP TABLE IF EXISTS tb_event_applications;
DROP TABLE IF EXISTS tb_coupons;
DROP TABLE IF EXISTS tb_api_trace_logs;


CREATE TABLE tb_member (
                           id          BIGINT AUTO_INCREMENT PRIMARY KEY, -- 시스템 PK
                           user_id     VARCHAR(50) NOT NULL,              -- 로그인 ID 또는 사번
                           name        VARCHAR(50) NOT NULL,              -- 성명
                           created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 계정 생성 일시
                           updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT uq_member_user_id UNIQUE (user_id)
);

COMMENT ON TABLE tb_member IS '회원 마스터 정보';
COMMENT ON COLUMN tb_member.id IS '회원 고유 식별자 (PK)';
COMMENT ON COLUMN tb_member.user_id IS '로그인 ID 또는 사번 (비즈니스 식별자)';
COMMENT ON COLUMN tb_member.name IS '성명';
COMMENT ON COLUMN tb_member.created_at IS '데이터 생성 일시';

CREATE TABLE tb_events (
                           id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                           title             VARCHAR(255) NOT NULL,             -- 이벤트 제목
                           start_at          TIMESTAMP NOT NULL,                -- 시작 일시
                           end_at            TIMESTAMP NOT NULL,                -- 종료 일시
                           draw_at           TIMESTAMP NOT NULL,                -- 추첨 일시
                           total_coin_limit  INT DEFAULT 900,                   -- 전체 코인 한도
                           issued_coin_count INT DEFAULT 0,                     -- 누적 발행 코인 수
                           user_coin_limit   INT DEFAULT 3,                     -- 인당 보유 한도
                           status            VARCHAR(20) DEFAULT 'READY',       -- 이벤트 상태
                           created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tb_events IS '이벤트 정책 및 통합 기간 관리';
COMMENT ON COLUMN tb_events.id IS '이벤트 고유 식별자 (PK)';
COMMENT ON COLUMN tb_events.title IS '이벤트 제목';
COMMENT ON COLUMN tb_events.start_at IS '이벤트 운영 시작일';
COMMENT ON COLUMN tb_events.end_at IS '이벤트 운영 마지막일 ';
COMMENT ON COLUMN tb_events.total_coin_limit IS '전체 발행 가능한 코인 총량';
COMMENT ON COLUMN tb_events.issued_coin_count IS '실시간 발행 누적 수 (조회 최적화용)';
COMMENT ON COLUMN tb_events.user_coin_limit IS '1인당 획득 가능한 최대 코인 수';
COMMENT ON COLUMN tb_events.status IS '이벤트 상태 (READY: 준비, ACTIVE: 진행, ENDED: 종료)';

CREATE TABLE tb_coin_issuances (
                                   id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   event_id    BIGINT NOT NULL,                       -- 이벤트 ID
                                   member_id   BIGINT NOT NULL,                       -- 회원 ID
                                   issued_seq  INT NOT NULL,                          -- 선착순 번호
                                   issuance_key VARCHAR(50),                          -- 발행요청 고유키
                                   status      VARCHAR(20) DEFAULT 'AVAILABLE',       -- 코인 상태
                                   created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   CONSTRAINT uq_event_coin_seq UNIQUE (event_id, issued_seq)
);

CREATE INDEX idx_coin_member_status ON tb_coin_issuances(member_id, event_id, status);

COMMENT ON TABLE tb_coin_issuances IS '코인 선착순 획득 이력 및 상태 정보';
COMMENT ON COLUMN tb_coin_issuances.id IS '코인 이력 고유 식별자 (PK)';
COMMENT ON COLUMN tb_coin_issuances.event_id IS '해당 이벤트 식별자';
COMMENT ON COLUMN tb_coin_issuances.member_id IS '코인을 획득한 회원 식별자';
COMMENT ON COLUMN tb_coin_issuances.issued_seq IS '이벤트별 코인 발행 순번 (1~900)';
COMMENT ON COLUMN tb_coin_issuances.status IS '코인 현재 상태 (AVAILABLE: 사용대기, USED: 사용완료)';



CREATE TABLE tb_event_applications (
                                       id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       event_id          BIGINT NOT NULL,                   -- 이벤트 ID
                                       member_id         BIGINT NOT NULL,                   -- 회원 ID
                                       coupon_id         BIGINT NOT NULL,                   -- 쿠폰 ID
                                       coin_id           BIGINT NOT NULL,                   -- 사용코인ID
                                       status            VARCHAR(20) NOT NULL,              -- 응모 상태
                                       request_id        VARCHAR(100),                      -- 응모요청 고유키
                                       applied_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       canceled_at       TIMESTAMP,
                                       created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                         -- 취소 일시
                                       updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_app_member_status ON tb_event_applications(member_id, event_id, status);

COMMENT ON TABLE tb_event_applications IS '사용자별 쿠폰 응모 및 취소 이력 정보';
COMMENT ON COLUMN tb_event_applications.id IS '응모 고유 식별자 (PK)';



CREATE TABLE tb_coupons (
                            id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                            event_id          BIGINT NOT NULL,
                            coupon_type       VARCHAR(10) NOT NULL,
                            coupon_group_code       VARCHAR(10) NOT NULL,
                            coupon_name       VARCHAR(100) NOT NULL,
                            issue_limit       INTEGER default 3,
                            issued_count      INTEGER default 0,
                            status            VARCHAR(20) DEFAULT 'AVAILABLE',
                            created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tb_coupons IS '당첨자 지급을 위한 쿠폰 마스터 정보';
COMMENT ON COLUMN tb_coupons.id IS '쿠폰 인스턴스 고유 식별자 (PK)';
COMMENT ON COLUMN tb_coupons.coupon_type IS '쿠폰종류';
COMMENT ON COLUMN tb_coupons.coupon_group_code IS '쿠폰 그룹코드';
COMMENT ON COLUMN tb_coupons.coupon_name IS '쿠폰 네임';
COMMENT ON COLUMN tb_coupons.issue_limit IS '쿠폰 발급 제한수 ';
COMMENT ON COLUMN tb_coupons.issued_count IS '쿠폰 발급 갯수 ';
COMMENT ON COLUMN tb_coupons.status IS '쿠폰 상태 (AVAILABLE: 발급가능, CLOSED: 발급불가)';



CREATE TABLE tb_api_trace_logs (
                                   trace_id      VARCHAR(50) PRIMARY KEY,
                                   api_name      VARCHAR(100),
                                   http_method   VARCHAR(10),
                                   status_code   INTEGER,
                                   request_data  TEXT,
                                   response_data TEXT,
                                   error_msg     TEXT,
                                   created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 데이터가 수정될 때마다 자동으로 현재 시간이 기록됨
                                   updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_created_at ON tb_api_trace_logs(created_at);