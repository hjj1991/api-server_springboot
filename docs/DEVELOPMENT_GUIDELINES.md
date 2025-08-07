## 개발 워크플로우 및 아키텍처 규칙

새로운 기능을, 특히 외부 시스템과 연동되는 API를 추가할 때는 다음 Hexagonal Architecture 원칙을 엄격히 준수해야 합니다.

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
