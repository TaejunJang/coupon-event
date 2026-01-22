# KidariQuest Event Service

이 프로젝트는 대규모 트래픽을 고려한 **이벤트 및 쿠폰 발급 시스템**입니다. 사용자에게 코인을 지급하고, 획득한 코인으로 다양한 이벤트 쿠폰에 응모할 수 있는 기능을 제공합니다.

도메인 주도 설계(DDD) 원칙을 기반으로 모듈화되어 있으며, Kafka를 활용한 비동기 처리를 통해 시스템의 결합도를 낮추고 확장성을 확보했습니다.

---

## Key Features (주요 특징)

*   **코인 시스템**: 이벤트 참여를 위한 재화(코인)의 발급, 사용, 잔액 관리.
*   **쿠폰 응모**: 동시성을 고려한 선착순/추첨제 쿠폰 응모 프로세스.
*   **이벤트 기반 처리**: Kafka를 이용해 코인 발급 요청과 쿠폰 응모 처리를 비동기적으로 수행하여 응답 속도 개선.
*   **통계 및 조회**: QueryDSL을 활용한 복잡한 동적 쿼리 및 페이징 조회 (사용자별 응모 현황, 이벤트별 통계 등).
*   **안정성**: 글로벌 예외 처리(`GlobalExceptionHandler`) 및 표준 응답 객체(`CommonResponse`) 적용.

---

## Tech Stack (기술 스택)

*   **Language**: Java 21
*   **Framework**: Spring Boot 3.4.1
*   **Database**: H2 (In-memory), Spring Data JPA
*   **Query**: QueryDSL 5.0 (Jakarta)
*   **Message Queue**: Apache Kafka (Spring Kafka)
*   **Build Tool**: Gradle

---

## Project Structure (프로젝트 구조)

이 프로젝트는 도메인별로 패키지를 분리하여 응집도를 높인 구조를 따릅니다.

```
src/main/java/com/kidari/event
├── common              # 공통 모듈 (Response, Exception, Utils)
├── global              # 전역 설정 (Config, Security 등)
├── domain              # 핵심 비즈니스 로직 (DDD)
│   ├── apiTrace        # API 호출 이력 추적
│   ├── coin            # 코인 발급 및 잔액 관리 도메인
│   ├── coupon          # 쿠폰 정보 및 통계 도메인
│   ├── entity          # JPA 엔티티 모음
│   ├── event           # 이벤트 정책 및 응모 도메인
│   ├── member          # 회원 정보 도메인
│   └── port            # 외부 통신을 위한 Port (Interface)
└── infrastructure      # 외부 시스템 연동 구현체 (Kafka Adapter 등)
```

### Architectural Highlights

1.  **Hexagonal Architecture (Ports & Adapters) 지향**
    *   도메인 로직이 외부 기술(Kafka 등) 의존하지 않도록 `Port` 인터페이스를 정의하고, `Infrastructure` 레이어에서 이를 구현하는 구조를 채택했습니다.

2.  **QueryDSL Custom Repository Pattern**
    *   복잡한 통계 및 동적 조회 요구사항을 해결하기 위해 `JpaRepository`와 `CustomRepository`를 결합하여 확장성 있는 데이터 접근 계층을 구현했습니다.

3.  **Event-Driven Architecture**
    *   `EventPort`를 통해 이벤트를 발행하고, `EventConsumer`에서 이를 구독하여 비즈니스 로직을 수행함으로써 트랜잭션 분리와 시스템 부하 분산을 꾀했습니다.

---

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.1/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.1/gradle-plugin/packaging-oci-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.1/reference/web/servlet.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

---

# API Documentation

모든 응답은 `CommonResponse` 공통 규격을 따릅니다.

**Common Response Structure**
```json
{
  "code": "SUCCESS",
  "message": "요청 성공",
  "time": "2026-01-20T10:00:00",
  "data": { ... }
}
```

## 1. Event API (`/api/events`)

### 1.1 쿠폰 응모 요청
소유한 응모 코인을 사용하여 특정 휴가쿠폰 응모를 요청합니다.

- **Method**: `POST`
- **Endpoint**: `/api/events/coupons`

**Request Body**

| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `userId` | String | O | 요청 유저 아이디 |
| `eventId` | Long | O | 요청 이벤트 번호 |
| `couponId` | Long | O | 요청 쿠폰 번호 |

**Response**
```json
{
  "code": "SUCCESS",
  "message": "쿠폰응모 신청 요청 완료",
  "time": "2026-01-20T10:00:00",
  "data": "쿠폰응모 신청 요청 완료"
}
```

---

### 1.2 쿠폰 응모 취소
휴가쿠폰 응모 내역을 취소하고 코인을 반환받습니다.

- **Method**: `PATCH`
- **Endpoint**: `/api/events/coupons/{eventApplicationId}/cancel`

**Path Parameters**

| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `eventApplicationId` | Long | O | 취소할 이벤트 응모 ID |

**Response**
```json
{
  "code": "SUCCESS",
  "message": "요청 성공",
  "time": "2026-01-20T10:00:00",
  "data": "취소완료"
}
```

---

### 1.3 내 응모 현황 조회
특정 사용자의 쿠폰 이벤트 응모 현황을 조회합니다.

- **Method**: `GET`
- **Endpoint**: `/api/events/applications`

**Query Parameters**

| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `userId` | String | O | 조회 요청 유저 아이디 |
| `applicationStatus` | Enum | X | 응모 상태 (APPLIED, CANCELED, WIN, LOSE) |
| `page` | Integer | X | 페이지 번호 (기본: 0) |
| `size` | Integer | X | 페이지 크기 (기본: 20) |
| `sort` | String | X | 정렬 조건 (예: id,desc) |

**Response**
```json
{
  "code": "SUCCESS",
  "message": "요청 성공",
  "time": "2026-01-20T10:00:00",
  "data": {
    "content": [
      {
        "eventApplicationId": 1,
        "userId": "kdr001",
        "eventTitle": "2026 1분기 휴가이벤트",
        "couponName": "휴가 1일권",
        "applicationStatus": "APPLIED",
        "appliedAt": "2026-01-20T10:00:00",
        "canceledAt": null
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

## 2. Coupon API (`/api/coupons`)

### 2.1 쿠폰별 응모 통계 조회
휴가 쿠폰별 전체 응모 현황 통계를 조회합니다.

- **Method**: `GET`
- **Endpoint**: `/api/coupons/events/stats`

**Query Parameters**

| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `couponGroupCode` | String | X | 쿠폰 그룹 코드 |
| `couponType` | Enum | X | 쿠폰 타입 |
| `page` | Integer | X | 페이지 번호 (기본: 0) |
| `size` | Integer | X | 페이지 크기 (기본: 20) |
| `sort` | String | X | 정렬 조건 |

**Response**
```json
{
  "code": "SUCCESS",
  "message": "요청 성공",
  "time": "2026-01-20T10:00:00",
  "data": {
    "content": [
      {
        "couponId": 1,
        "eventName": "2026 1분기 이벤트",
        "couponName": "휴가 1일권",
        "userId": "kdr001",
        "userName": "홍길동",
        "appliedAt": "2026-01-20T10:00:00",
        "canceledAt": null
      }
    ],
    "page": 0,
    "size": 20,
    ...
  }
}
```

## 3. Coin API (`/api/coins`)

### 3.1 응모 코인 획득 요청
쿠폰 이벤트에 응모할 수 있는 코인을 획득합니다.

- **Method**: `POST`
- **Endpoint**: `/api/coins/acquisitions`

**Request Body**

| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `userId` | String | O | 요청 유저 아이디 |
| `eventId` | Long | O | 요청 이벤트 아이디 |

**Response**
```json
{
  "code": "SUCCESS",
  "message": "응모코인 신청 요청 완료",
  "time": "2026-01-20T10:00:00",
  "data": "응모코인 신청 요청 완료"
}
```

---

### 3.2 코인 잔액 조회
사용자가 보유한 응모 코인 수량을 조회합니다.

- **Method**: `GET`
- **Endpoint**: `/api/coins/balances`

**Query Parameters**

| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `userId` | String | O | 요청 유저 아이디 |
| `eventId` | Long | X | 이벤트 아이디 |
| `status` | Enum | X | 코인 상태 (AVAILABLE, USED) |

**Response**
```json
{
  "code": "SUCCESS",
  "message": "조회 성공",
  "time": "2026-01-20T10:00:00",
  "data": {
    "balance": 5
  }
}
```

---

### 3.3 이벤트 코인 통계 조회
이벤트의 남은 코인 수와 사용자별 획득 현황을 조회합니다.

- **Method**: `GET`
- **Endpoint**: `/api/coins/stats`

**Query Parameters**

| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `eventId` | Long | X | 이벤트 아이디 |
| `page` | Integer | X | 페이지 번호 (기본: 0) |
| `size` | Integer | X | 페이지 크기 (기본: 20) |
| `sort` | String | X | 정렬 조건 |

**Response**
```json
{
  "code": "SUCCESS",
  "message": "요청 성공",
  "time": "2026-01-20T10:00:00",
  "data": {
    "remainCoin": 100,
    "userCoinBalanceList": {
      "content": [
        {
          "userId": "kdr001",
          "eventId": 1,
          "eventName": "2026 1분기 이벤트",
          "coin": 3
        }
      ],
      "page": 0,
      "size": 20,
      ...
    }
  }
}
```

---

## 프로젝트 회고

### 🚀 직면했던 문제와 해결 과정
- **Kafka 인프라 설정 이슈**: 로컬 환경에서 단일 브로커 사용 시 `Replication Factor` 설정 누락으로 인한 무한 루프 발생. `docker-compose` 설정을 통해 복제 계수를 1로 조정하여 해결.
- **순환 참조(Circular Dependency)**: 서비스 계층에서 자기 자신을 주입받는 실수로 애플리케이션 기동 실패. 불필요한 의존성을 제거하여 해결.
- **JPA 묵시적 조인 발생**: 단순 외래키 조건 조회 시 JPA가 불필요하게 `LEFT JOIN`을 생성하는 문제 확인. 성능 최적화가 필요한 경우 `@Query`와 JPQL을 사용해 명시적으로 ID 기반 조회를 하도록 고려함.
- **QueryDSL DTO 매핑 오류**: `Projections.constructor` 사용 시 파라미터 타입(특히 `Integer` vs `Long`)과 순서 불일치로 인한 런타임 에러 발생. DTO 필드 타입을 DB count 결과 타입에 맞게 `Long`으로 통일하여 해결.

### 💡 Lessons Learned (배운 점)
- QueryDSL을 도입하면서 복잡한 동적 쿼리를 타입 안정성을 보장하며 작성하는 방법을 익힘.
- Kafka를 통한 비동기 처리 시 멱등성(`issuanceKey`) 보장의 중요성을 다시금 깨달음.
- 도메인 간의 결합도를 낮추기 위해 `Port`와 `Adapter` 패턴을 적용해 보며 Hexagonal 아키텍처의 이점을 경험함.

### 🛠️ Future Improvements (향후 개선 사항)
- **동시성 제어 고도화**: 선착순 코인 발급 시 Redis 분산 락 등을 도입하여 DB 부하를 줄이고 정확한 처리를 보장하는 로직 추가.
- **테스트 코드 보강**: JUnit5와 Testcontainers를 활용하여 Kafka와 DB 연동 테스트를 더욱 촘촘하게 구성.
- **모니터링**: Prometheus와 Grafana를 연동하여 시스템 메트릭 시각화.
