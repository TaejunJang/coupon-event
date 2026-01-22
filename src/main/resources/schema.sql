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
COMMENT ON COLUMN tb_member.user_id IS '로그인 ID';
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
COMMENT ON COLUMN tb_events.draw_at IS '추첨 일시';
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
COMMENT ON COLUMN tb_coin_issuances.issuance_key IS '멱등성 유지를 위한 요청키';
COMMENT ON COLUMN tb_coin_issuances.status IS '코인 현재 상태 (AVAILABLE: 사용대기, USED: 사용완료)';



CREATE TABLE tb_event_applications (
                                       id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       event_id          BIGINT NOT NULL,                   -- 이벤트 ID
                                       member_id         BIGINT NOT NULL,                   -- 회원 ID
                                       coupon_id         BIGINT NOT NULL,                   -- 쿠폰 ID
                                       coin_id           BIGINT NOT NULL,                   -- 사용코인ID
                                       status            VARCHAR(20) NOT NULL,              -- 응모 상태
                                       request_id        VARCHAR(100),                      -- 응모요청 고유키
                                       applied_at        TIMESTAMP,                         -- 응모일자
                                       canceled_at       TIMESTAMP,                         -- 취소일자
                                       created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_app_member_status ON tb_event_applications(member_id, event_id, status);

COMMENT ON TABLE tb_event_applications IS '사용자별 쿠폰 응모 및 취소 이력 정보';
COMMENT ON COLUMN tb_event_applications.id IS '응모 고유 식별자 (PK)';
COMMENT ON COLUMN tb_event_applications.event_id IS '이벤트 ID';
COMMENT ON COLUMN tb_event_applications.member_id IS '회원 ID';
COMMENT ON COLUMN tb_event_applications.coupon_id IS '쿠폰 ID';
COMMENT ON COLUMN tb_event_applications.coin_id IS '사용코인ID';
COMMENT ON COLUMN tb_event_applications.status IS '응모 상태';
COMMENT ON COLUMN tb_event_applications.request_id IS '응모요청 고유키';
COMMENT ON COLUMN tb_event_applications.applied_at IS '응모일자';
COMMENT ON COLUMN tb_event_applications.canceled_at IS '취소일자';



CREATE TABLE tb_coupons (
                            id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                            event_id          BIGINT NOT NULL,                          -- 이벤트 ID
                            coupon_type       VARCHAR(10) NOT NULL,                     -- 쿠폰타입 (EVENT)
                            coupon_group_code       VARCHAR(10) NOT NULL,               -- 쿠폰마스터그룹코드
                            coupon_name       VARCHAR(100) NOT NULL,                    -- 쿠폰명
                            issue_limit       INTEGER default 3,                        -- 발행 제한 수
                            issued_count      INTEGER default 0,                        -- 발행숫자
                            status            VARCHAR(20) DEFAULT 'AVAILABLE',          -- 쿠폰 발급가능 여부
                            created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tb_coupons IS '당첨자 지급을 위한 쿠폰 마스터 정보';
COMMENT ON COLUMN tb_coupons.id IS '쿠폰 인스턴스 고유 식별자 (PK)';
COMMENT ON COLUMN tb_coupons.event_id IS '이벤트 ID';
COMMENT ON COLUMN tb_coupons.coupon_type IS '쿠폰종류';
COMMENT ON COLUMN tb_coupons.coupon_group_code IS '쿠폰 그룹코드';
COMMENT ON COLUMN tb_coupons.coupon_name IS '쿠폰 네임';
COMMENT ON COLUMN tb_coupons.issue_limit IS '쿠폰 발급 제한수 ';
COMMENT ON COLUMN tb_coupons.issued_count IS '쿠폰 발급 갯수 ';
COMMENT ON COLUMN tb_coupons.status IS '쿠폰 상태 (AVAILABLE: 발급가능, CLOSED: 발급불가)';



CREATE TABLE tb_api_trace_logs (
                                   trace_id      VARCHAR(50) PRIMARY KEY,                   -- 요청 고유번호
                                   api_name      VARCHAR(100),                              -- api url
                                   http_method   VARCHAR(10),                               -- http메소드 (POST,GET)
                                   status_code   INTEGER,                                   -- 응답 상태 (200,400,500)
                                   request_data  TEXT,                                      -- 요청 일자
                                   response_data TEXT,                                      -- 응답데이터
                                   error_msg     TEXT,                                      -- 에러메세지
                                   created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_created_at ON tb_api_trace_logs(created_at);

COMMENT ON TABLE tb_api_trace_logs IS 'API 요청/응답 로그';
COMMENT ON COLUMN tb_api_trace_logs.trace_id IS '요청 고유번호';
COMMENT ON COLUMN tb_api_trace_logs.api_name IS 'api url';
COMMENT ON COLUMN tb_api_trace_logs.http_method IS 'http메소드 (POST,GET)';
COMMENT ON COLUMN tb_api_trace_logs.status_code IS '응답 상태 (200,400,500)';
COMMENT ON COLUMN tb_api_trace_logs.request_data IS '요청 데이터';
COMMENT ON COLUMN tb_api_trace_logs.response_data IS '응답데이터';
COMMENT ON COLUMN tb_api_trace_logs.error_msg IS '에러메세지';