# Kidari Coupon Event Application Service

이 프로젝트는 대규모 트래픽을 고려한 **이벤트 응모코인 획득 쿠폰이벤트 신청 시스템**입니다. 
사용자에게 코인을 지급하고, 획득한 코인으로 이벤트 쿠폰에 응모할 수 있는 기능을 제공합니다.

도메인 주도 설계(DDD) 원칙을 기반으로 모듈화되어 있으며, Kafka를 활용한 비동기 처리를 통해 
시스템의 결합도를 낮추고 확장성을 확보했습니다.

---

## 주요 특징

*   **코인 시스템**: 이벤트 참여를 위한 재화(코인)의 발급, 사용.
*   **코인획득,쿠폰 응모**: 동시성 및 대규모 응모트래픽을 고려한 선착순/이벤트 응모 프로세스.
*   **이벤트 기반 처리**: Kafka를 이용해 코인 발급 요청과 쿠폰 응모 처리를 비동기적으로 수행하여 동시성처리 대규모 트래픽관리.
*   **조회**: QueryDSL을 활용한 복잡한 동적 쿼리 및 페이징 조회 (사용자별 응모 현황, 이벤트 현황 등).
*   **API로그**: API 요청/응답 로그 관련  (`ApiTraceFilter`) 활용한 로그 데이터 적재.

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

이 프로젝트는 도메인별로 패키지를 분리하고, 각 도메인 내부에서 계층(Controller, Service, Repository, Entity 등)을 관리하는 구조를 따릅니다.

```
src/main/java/com/kidari/event
├── common              # 공통 모듈 (API 응답 규격, 예외 처리, 공통 설정, 유틸리티 등)
├── domain              # 핵심 비즈니스 로직 및 도메인 모델
│   ├── apiTrace        # API 요청/응답 이력 추적 및 로그 관리
│   ├── coin            # 코인 발급, 잔액 관리 및 획득 이벤트 처리
│   ├── coupon          # 쿠폰 정보 관리 및 통계 조회
│   ├── event           # 이벤트 정책 관리 및 쿠폰 응모/취소 로직
│   ├── member          # 회원 정보 관리
│   └── entity          # 공통 엔티티 (BaseTimeEntity 등)
└── infrastructure      # 외부 인프라스트럭처 연동 (Kafka Adapter, Config 등)
```

### Domain Internal Structure
각 도메인 패키지(coin, coupon, event 등)는 내부적으로 다음과 같은 구조를 가집니다.

*   **controller**: REST API 엔드포인트 및 요청/응답 DTO
*   **service**: 비즈니스 로직 처리 및 Command 객체 정의
*   **repository**: 데이터 접근 인터페이스 및 QueryDSL 구현체
*   **entity**: JPA 엔티티 정의
*   **port**: 외부 시스템 연동을 위한 인터페이스 (Port)
*   **dto**: 서비스 및 레포지토리 계층에서 사용하는 데이터 전송 객체

### Architectural Highlights

1.  **Ports & Adapters**
    *   도메인 로직과 외부 인프라(Kafka)를 `Port` 인터페이스로 분리하여 결합도를 낮추고 유연성을 확보했습니다.
2.  **Event-Driven Architecture**
    *   Kafka를 통한 비동기 메시징으로 대규모 트래픽 환경에서 트랜잭션을 분리하고 시스템 부하를 효율적으로 분산했습니다.
3.  **Type-Safe Dynamic Queries**
    *   QueryDSL을 활용하여 복잡한 검색 조건과 통계 쿼리를 타입 안정성을 보장하며 효율적으로 처리했습니다.

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

### 설계와 고민의 과정
처음 요구사항을 접했을 때는 간단해 보였지만, 테이블 구조와 패키지 구조의 확장성 범위를 결정하는 데 깊은 고민이 필요했습니다. 
일주일이라는 시간 동안 "어디까지 확장성을 고려해야 하는가?"를 끊임없이 질문했고, 결국 과제 요구사항을 충실히 따르면서도 오버엔지니어링을 피하는 최적의 구조를 잡는 데 집중했습니다.

### 기술 선정과 확장성
대규모 트래픽 처리를 위한 메시지 큐 기술 선정 과정에서, 현재의 요구사항을 충족하면서도 향후 시스템 확장에 유리한 기술이 무엇인지 깊이 있게 탐구했습니다. 
단순한 순차 처리를 넘어, 미래의 확장 가능성까지 고려한 기술 스펙 결정 과정은 아키텍처를 보는 시야를 넓혀주었습니다.

### 로깅 전략과 유지보수
API 로그 적재 위치에 대한 고민 또한 인상적인 경험이었습니다. 유지보수의 편의성과 요청/응답의 완벽한 추적(Trace)이라는 두 마리 토끼를 잡기 위해 필터, 인터셉터, AOP 등 다양한 포인트를 고려하며 최적의 위치를 선정했습니다. 
이 과정에서 평소 깊게 다루지 않았던 로깅 전략에 대해 심도 있게 고민할 수 있었습니다.

### 마치며
이번 프로젝트는 단순한 기능 구현을 넘어, 프로젝트 성격에 맞는 적절한 기술 선정과 방향성 설정에 대해 한 단계 발전된 안목을 기를 수 있는 값진 시간이었습니다.
