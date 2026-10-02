# 💱 Exchange Rate Checker

Spring Boot 기반의 실시간 환율 조회 및 통화 변환 서비스입니다.

## 📌 주요 기능

| 기능 | 설명 |
|------|------|
| 실시간 환율 조회 | 기준 통화 대비 주요 14개 통화 환율 표시 |
| 통화 변환 | 입력 금액을 원하는 통화로 즉시 변환 |
| 조회 이력 | 최근 변환 이력 20건 자동 저장 및 조회 |
| 캐싱 | Caffeine 캐시 적용 (10분 TTL) |

## 🛠 기술 스택

- **Backend**: Spring Boot 3.2, Java 17
- **DB**: H2 (In-Memory)
- **Cache**: Caffeine Cache
- **HTTP Client**: Spring WebFlux (WebClient)
- **View**: Thymeleaf + HTML/CSS/JS (Vanilla)
- **Build**: Gradle

## 🚀 실행 방법

```bash
cd exchange-rate-checker
./gradlew bootRun
```

브라우저에서 `http://localhost:8080` 접속

H2 콘솔: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:exchangedb`)

## 📡 REST API

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/exchange/rates?base=USD` | 환율 목록 조회 |
| GET | `/api/exchange/convert?from=USD&to=KRW&amount=100` | 통화 변환 |
| GET | `/api/exchange/currencies` | 지원 통화 목록 |
| GET | `/api/exchange/history` | 최근 조회 이력 (20건) |

## 📁 프로젝트 구조

```
src/main/java/com/example/exchangerate/
├── ExchangeRateApplication.java     # 메인 클래스
├── config/
│   ├── CacheConfig.java             # Caffeine 캐시 설정
│   └── WebClientConfig.java         # WebClient Bean 설정
├── controller/
│   ├── ExchangeRateController.java  # REST API 컨트롤러
│   └── ViewController.java          # Thymeleaf 페이지 컨트롤러
├── service/
│   └── ExchangeRateService.java     # 비즈니스 로직
├── client/
│   └── ExchangeRateApiClient.java   # 외부 API 호출
├── dto/
│   ├── ExchangeRateApiResponse.java # 외부 API 응답 DTO
│   ├── RatesResponse.java           # 환율 응답 DTO
│   └── ConvertResponse.java         # 변환 응답 DTO
├── entity/
│   └── ExchangeRateHistory.java     # 조회 이력 엔티티
└── repository/
    └── ExchangeRateHistoryRepository.java
```

## 🔗 사용 외부 API

- **ExchangeRate-API** (무료, 인증 불필요)
  - URL: `https://open.er-api.com/v6/latest/{base}`
  - 제한: 일 1,500회 요청

## 🔮 향후 확장 계획

- [ ] Redis 캐시 전환
- [ ] 환율 추이 차트 (Chart.js)
- [ ] 특정 환율 도달 알림 (이메일/Slack)
- [ ] MySQL 연동 (운영 환경)
- [ ] Spring Security + JWT 인증
