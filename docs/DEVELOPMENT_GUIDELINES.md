## 개발 워크플로우 및 아키텍처 규칙

### 세션 시작 체크

- 신규 세션에서는 반드시 루트 `AGENTS.md`의 `SESSION ONBOARDING (READ FIRST)`를 먼저 확인합니다.
- 본 문서는 구현/테스트/문서화 규칙의 상세 기준으로 사용합니다.

새로운 기능을, 특히 외부 시스템과 연동되는 API를 추가할 때는 다음 Hexagonal Architecture 원칙을 엄격히 준수해야 합니다.

### 공통 코딩 규칙 (필수)

1. **매직값 금지**
   - 의미 있는 문자열/숫자/URI/헤더값은 코드에 하드코딩하지 않습니다.
   - 공통 상수(`common/*Constants`) 또는 설정 프로퍼티를 사용합니다.

2. **환경 의존 값 중앙관리**
   - 도메인, 외부 API URL, Problem type base URI 등 환경에 따라 바뀌는 값은
     `application.yml` + 환경변수 override로 관리합니다.
   - 코드에서 직접 `https://...`, `localhost`, 포트 숫자를 박아넣지 않습니다.

3. **에러 응답 표준화**
   - API 에러는 RFC9457 `ProblemDetail` 형식을 사용합니다.
   - 에러 코드는 `ErrConst`를 단일 소스로 사용하고, 상태코드/메시지 매핑을 분산하지 않습니다.
   - 인증/인가/전역 예외 모두 동일한 에러 포맷(`application/problem+json`)을 유지합니다.

4. **Controller 규칙 표준화**
   - 모든 컨트롤러는 클래스 상단에 `@RequestMapping("/resource-base")`를 선언하고, 메서드에는 하위 경로만 선언합니다.
   - 컨트롤러의 응답 타입은 반드시 `*Response` postfix 클래스를 사용합니다.
   - 내부 계층(application/service/adapter-out) 전용 `*Dto`를 컨트롤러에서 직접 반환하지 않습니다.
   - 버전 헤더 조건은 문자열 하드코딩 대신 공통 상수(`ApiVersionConstants.HEADER_V1`)를 사용합니다.
   - 컨트롤러 함수명은 동사+리소스 패턴으로 통일합니다.
     - 조회 목록: `list<ResourcePlural>`
     - 단건 조회: `get<Resource>ById`
     - 생성/트리거: `create<Resource>`, `trigger<Action>`

### 헥사고날 아키텍처 기반 테스트 규약

레이어가 아니라 **경계(Port/Adapter) 기준**으로 테스트를 작성합니다.

1. **Domain 테스트 (순수 단위 테스트)**
   - 대상: `domain/**`
   - 목적: 엔티티/값객체/도메인 규칙 검증
   - 규칙: Spring 컨텍스트/DB/네트워크 없이 실행

2. **Application 테스트 (UseCase 단위 테스트)**
   - 대상: `application/service/**` (Input Port 구현체)
   - 목적: 유스케이스 흐름, 분기, 정렬, 예외 매핑 검증
   - 규칙: Output Port는 Mock/Fake로 대체하고 비즈니스 로직에만 집중

3. **Inbound Adapter 테스트 (웹 슬라이스 테스트)**
   - 대상: `adapter/input/web/**`
   - 목적: 요청/응답 매핑, Validation, 인증/인가, 에러 포맷 검증
   - 규칙: `@WebMvcTest` + MockMvc 사용, UseCase는 Mock 처리
   - 필수: `ProblemDetail` 응답 스키마(401/403/400/404) 검증

4. **Outbound Adapter 테스트 (통합 테스트)**
   - 대상: `adapter/out/persistence/**`
   - 목적: 실제 쿼리/매핑/페이징/정렬 동작 검증
   - 규칙: `@DataJpaTest` + Testcontainers PostgreSQL 사용
   - 금지: 임베디드 DB(H2 등)로 PostgreSQL 대체 검증

5. **테스트 작성 원칙**
   - 테스트 패키지는 운영 코드 구조와 1:1 미러링
   - 포트 계약(Input/Output Port)의 입출력/예외를 테스트 이름으로 명확히 표현
   - 테스트 데이터/상수도 매직값을 피하고 fixture/factory로 중앙화

6. **코드 변경 시 테스트 실행 규칙 (필수)**
   - 코드 변경이 발생하면, 변경된 경계와 직접 연관된 테스트를 우선 실행합니다.
   - 작업 완료 전 최소 `./gradlew test` 또는 `./gradlew build`로 최종 검증합니다.
   - 테스트 미실행/실패 상태로 작업을 종료하지 않습니다.
   - 실패 시 원인과 조치 내용을 작업 결과에 함께 기록합니다.

**신규 스트리밍 API 개발 예시:**

1.  **Input Port (UseCase) 정의:**
    *   `application/port/input/{domain}` 패키지에 유스케이스 인터페이스를 생성합니다.
    *   예: `StreamFinancialProductSearchUseCase.kt`

2.  **Output Port 정의:**
    *   `application/port/out/{domain}` 패키지에 외부 시스템 연동을 위한 인터페이스(Port)를 생성합니다.
    *   예: `StreamFinancialProductSearchPort.kt`

3.  **서비스 (UseCase 구현체) 개발:**
    *   `application/service/{domain}` 패키지에서 Input Port를 구현합니다.
    *   이 서비스는 비즈니스 로직에 집중하며, 실제 외부 통신은 Output Port에 위임합니다.
    *   예: `StreamFinancialProductSearchService.kt` (내부적으로 `StreamFinancialProductSearchPort`를 호출)

4.  **어댑터 (Adapter) 개발:**
    *   `adapter/out/{type}` 패키지에서 Output Port를 구현합니다.
    *   `WebClient`, `JPA Repository` 등 실제 외부 시스템과 통신하는 로직은 이 어댑터에 위치합니다.
    *   예: `adapter/out/web/StreamFinancialProductSearchAdapter.kt`

5.  **입력 어댑터 (Controller) 수정:**
    *   `adapter/input/web/{domain}` 패키지의 컨트롤러에서 Input Port(UseCase)를 주입받아 사용합니다.
    *   예: `FinancialController.kt`에서 `StreamFinancialProductSearchUseCase`를 호출.
