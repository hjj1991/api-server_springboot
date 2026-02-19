# API Server Handoff (LLM-Ready Contract)

## 1) Goal
이 문서 하나만으로 API 서버(또는 LLM 에이전트)가 배치 산출물을 정확히 소비/서빙할 수 있도록
데이터 계약, 변경 규칙, 실패/복구 전략을 명시한다.

---

## 2) Authoritative Data Sources

### 2.1 Live Snapshot (API 기본 조회 원본)
- `financial_company`
- `financial_company_area`
- `financial_product`
- `financial_product_option`

### 2.2 History (변경 추적 / 재동기화 원본)
- `financial_product_history` (JSONB payload)
- `financial_product_rate_history`

### 2.3 Event Stream (실시간 반영 트리거)
- PGMQ queue: `product_change_events`

원칙:
- 실시간 반영: queue 우선
- 정합성 보정: history 재스캔

---

## 3) Key Model Contracts

### 3.1 Natural Keys
- 회사: `financial_company.financial_company_code`
- 상품: `(financial_company_id, financial_product_code, financial_product_type)`
- 옵션: `(financial_product_id, interest_rate_type, reserve_type, deposit_period_months)`

### 3.2 FK Policy
- Live snapshot 테이블은 FK 유지
  - `financial_company_area.financial_company_id -> financial_company.financial_company_id`
  - `financial_product.financial_company_id -> financial_company.financial_company_id`
  - `financial_product_option.financial_product_id -> financial_product.financial_product_id`
- History hypertable은 FK 미적용(append/retention/compression 운영 효율 우선)

### 3.3 Nullability You Must Accept
- `financial_product.embedding_vector`: `NULL` 가능
- `financial_product.max_limit`: `NULL` 가능
- `financial_product.dcls_end_day`: `NULL` 가능
- `financial_product_option.reserve_type`: `NULL` 가능

---

## 4) Batch Lifecycle (What Updates When)

실행 순서(고정):
1. `FINANCIAL_COMPANY_FETCH_STEP` (회사 staging)
2. `FINANCIAL_COMPANY_SYNC_STEP` (회사 live 반영)
3. `FINANCIAL_PRODUCT_SAVINGS_FETCH_STEP` (상품 staging:SAVINGS)
4. `FINANCIAL_PRODUCT_SAVINGS_SYNC_STEP` (상품 live 반영:SAVINGS)
5. `FINANCIAL_PRODUCT_INSTALLMENT_SAVINGS_FETCH_STEP` (상품 staging:INSTALLMENT)
6. `FINANCIAL_PRODUCT_INSTALLMENT_SAVINGS_SYNC_STEP` (상품 live 반영:INSTALLMENT)
7. `FINANCIAL_PRODUCT_STATUS_UPDATE_STEP` (미관측 상품 `DELETED` 전환)
8. `FINANCIAL_PRODUCT_HISTORY_PGMQ_SYNC_STEP` (history 적재 + 이벤트 enqueue)

staging 처리 규칙:
- `financial_company_staging`: 실행 시작 시 `TRUNCATE`
- `financial_product_staging`: 타입별(`SAVINGS`/`INSTALLMENT_SAVINGS`) `DELETE`

---

## 5) Status/Freshness Contract

- `last_seen_at`은 “상품을 읽은 시각”이 아니라 **Job 시작 시각(runStartedAt)** 으로 갱신된다.
- 상태 업데이트 스텝에서 조건을 만족하면 `status=DELETED`로 전환한다.
  - 조건: `last_seen_at IS NULL OR last_seen_at < runStartedAt`
  - 대상 타입: `SAVINGS`, `INSTALLMENT_SAVINGS`
- 이후 실행에서 다시 관측되면 writer가 `ACTIVE`로 재활성화한다.

API 기본 조회 규칙:
- 사용자 노출 목록 기본 필터: `status='ACTIVE'`

---

## 6) History Write Contract (Most Important)

### 6.1 `financial_product_history` 적재 조건
다음이 모두 동일하면 **미적재**:
- `status`
- `product_content_hash`
- `payload` (DB `jsonb` 동등성 기준)

즉, 원천 변경이 없으면 history row가 증가하지 않아야 한다.

### 6.2 `financial_product_rate_history` 적재 규칙
- product history를 적재하는 경우에만 함께 적재한다.
- insert 충돌키:
  - `(observed_at, financial_product_id, interest_rate_type, deposit_period_months)`
- 충돌 시 `DO UPDATE`로 upsert 처리한다.

주의:
- `reserve_type`은 충돌키에 포함되지 않는다.
- 따라서 같은 키 조합에서 `reserve_type`이 달라지면 최신값으로 update될 수 있다.

### 6.3 `observed_at` semantics
- `observed_at`은 writer chunk 처리 시점(`OffsetDateTime.now()`)이다.
- 같은 chunk 내 여러 상품은 동일 `observed_at`을 공유할 수 있다.

---

## 7) Queue Event Contract (PGMQ)

Queue name: `product_change_events`

payload shape:
```json
{
  "event_type": "NEW_PRODUCT | STATUS_CHANGED | CONTENT_CHANGED",
  "occurred_at": "2026-02-16T00:00:00Z",
  "financial_product_id": 123,
  "financial_company_id": 45,
  "financial_product_code": "PRDT-001",
  "status": "ACTIVE | DELETED"
}
```

event_type 결정:
- `NEW_PRODUCT`: 이전 snapshot 없음
- `STATUS_CHANGED`: status 변경
- `CONTENT_CHANGED`: content hash 또는 payload 변경

전달 보장:
- at-least-once 전제로 소비(중복 수신 가능)

소비 규칙:
- 처리 성공 후 archive/delete
- 실패 시 archive/delete 금지(재처리 허용)
- 소비 로직은 멱등(idempotent)이어야 함

---

## 8) API Build Guidance (Recommended Endpoints)

### 8.1 Core Read APIs
- 상품 목록 API (기본 `ACTIVE` 필터)
- 상품 상세 API (회사/옵션 조인)
- 변경 이력 API (`financial_product_history` 기반)

### 8.2 Sync/Operations APIs
- 마지막 동기화 시각 조회
- 재동기화 트리거(운영자용)

### 8.3 Suggested Cursor Order
- history replay 기본 정렬: `observed_at DESC, financial_product_id DESC`
- 동점 처리용 보조 키를 항상 포함해 cursor 안정성 보장

---

## 9) Recovery / Reconciliation Playbook

Queue 유실/지연/소비중단 시:
1. 마지막 처리 시각 checkpoint 확보
2. `financial_product_history`를 checkpoint 이후 구간 재조회
3. API read model을 upsert로 보정
4. queue 소비 재개

운영 패턴:
- queue 기반 near-real-time + history 기반 주기 보정(하이브리드)

---

## 10) Source Payload Notes

- `financial_company.source_payload`, `financial_product.source_payload`, `financial_product_option.source_payload`는 `TEXT` 컬럼이다.
- 저장 값은 JSON 문자열이며, API는 이를 원본 추적/디버깅 용도로만 사용한다.
- API 핵심 비즈니스 로직은 live snapshot 컬럼 + history payload(JSONB) 기준으로 작성한다.

---

## 11) Runtime/Environment Contract

- Job name: `FINANCIAL_COMPANY_SYNC_JOB`
- Profile: `local | prod`
- 로컬 기본 DB: `jdbc:postgresql://localhost:5432/api_server`
- 필수 키:
  - `FSS_AUTH_KEY`
  - `GEMINI_AUTH_KEY`

---

## 12) Acceptance Checklist (for API Team / LLM)

- [ ] 기본 목록 API가 `ACTIVE`만 노출한다.
- [ ] 동일 원천데이터 재실행 시 `financial_product_history` row가 불필요하게 증가하지 않는다.
- [ ] queue 중복 이벤트를 멱등 처리한다.
- [ ] queue 장애 후 history replay로 정합성 복구 가능하다.
- [ ] `embedding_vector IS NULL`을 정상 케이스로 처리한다.
