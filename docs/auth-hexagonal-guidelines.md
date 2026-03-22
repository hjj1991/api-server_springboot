# Auth Hexagonal Guidelines

## 목적

`auth` 패키지에서 어디까지 추상화하면 충분히 헥사고날스럽고, 어디부터는 과한 추상화인지 기준을 정리합니다.

## 현재 권장 경계

### 1. 유지해야 하는 경계

- `application/service/auth`는 JPA entity, JPA repository, `StringRedisTemplate`, `NamedParameterJdbcTemplate`를 직접 알지 않습니다.
- `application/service/auth`는 `application/port/input/auth`, `application/port/out/auth`, `domain/auth`만 봅니다.
- JPA dirty checking, Redis key 구조, PGMQ SQL은 adapter 내부 구현으로 숨깁니다.

### 2. 현재 구조에서 좋은 부분

- 로그인 조회와 성공 기록은 port로 분리되어 있습니다.
  - [LoadLocalLoginAccountPort.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/application/port/out/auth/LoadLocalLoginAccountPort.kt)
  - [RecordLocalLoginSuccessPort.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/application/port/out/auth/RecordLocalLoginSuccessPort.kt)
- 애플리케이션 서비스는 도메인 모델을 기준으로 판단합니다.
  - [LocalLoginAccount.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/domain/auth/LocalLoginAccount.kt)
  - [AuthUser.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/domain/auth/AuthUser.kt)
- persistence adapter 안에서는 JPA 더티체킹을 써도 됩니다.
  - [AuthPersistenceAdapter.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/adapter/out/persistence/auth/AuthPersistenceAdapter.kt)

### 3. 이 정도면 충분히 헥사고날스럽다고 보는 영역

- application service가 `PasswordEncoder`, `JwtEncoder` 같은 프레임워크 빈을 간접적으로 쓰는 것
- application service가 `Clock`, `ConfigurationProperties`를 받는 것
- adapter 내부에서 entity를 domain으로 매핑하는 것
- adapter 내부에서 dirty checking을 사용하는 것

## 과한 추상화로 보기 쉬운 영역

### 1. 단순 값 생성기까지 전부 port로 감싸는 것

- `UUID` 생성
- 간단한 시간 조회
- 내부 전용 해시/암호화 유틸

이건 현재처럼 [AuthCryptoService.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/application/service/auth/AuthCryptoService.kt) 수준으로 두는 편이 낫습니다.

### 2. 토큰 발급기까지 무조건 외부 포트로 빼는 것

- [AccessTokenIssuer.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/application/service/auth/AccessTokenIssuer.kt)는 지금 단계에서는 충분히 괜찮습니다.
- JWT 발급 전략이 서비스 외부 시스템과 강하게 결합되기 전까지는 내부 서비스로 유지해도 됩니다.

### 3. service 하나당 port를 지나치게 잘게 쪼개는 것

- use case 하나에서만 쓰는 단순 동작을 port로 과도하게 쪼개면 읽기 어려워집니다.
- 조회/저장/외부 연동 경계가 분명한 것만 port로 두는 편이 낫습니다.

## 현재 auth 패키지에서 권장 기준

### 반드시 port로 둘 것

- 사용자/인증정보 조회
- 로그인 성공 기록
- 중복 이메일 확인
- 로컬 계정 생성
- refresh 세션 관리
- access denylist 관리
- token version 저장소/캐시 접근
- 이메일 발송 큐 적재

### service 내부에 둬도 되는 것

- 이메일 정규화
- 해시 생성
- verification token 생성
- access token claim 조립
- 비즈니스 조합 로직

## 현재 남겨도 되는 직접 의존

- [AuthSessionService.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/application/service/auth/AuthSessionService.kt) -> [ManageUserTokenVersionUseCase.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/application/port/input/auth/ManageUserTokenVersionUseCase.kt)
- [AccessTokenStateValidator.kt](/Users/jaejeonghwang/project/api-server_springboot/src/main/kotlin/com/hjj/apiserver/config/AccessTokenStateValidator.kt) -> `GetCurrentUserTokenVersionPort`, `AccessTokenDenylistPort`

이 정도는 애플리케이션 경계와 보안 경계가 명확해서 괜찮습니다.

## 앞으로 리팩터링 우선순위

1. auth controller에서 input port만 보도록 유지
2. auth adapter에 mapper가 더 커지면 converter 파일로 분리
3. SNS 로그인 추가 시 `LOCAL`과 동일하게 `auth_identities` 중심으로 확장
4. 이메일 발송 worker가 생기면 queue payload도 domain event처럼 정리 검토

## 한 줄 기준

- `ORM/Redis/SQL 디테일이 application에 새어 나오면 정리 대상`
- `단순 유틸/값 생성까지 전부 추상화하려 들면 과한 설계`
