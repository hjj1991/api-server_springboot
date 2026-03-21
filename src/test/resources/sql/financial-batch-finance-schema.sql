CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS financial_company (
    financial_company_id BIGSERIAL PRIMARY KEY,
    financial_company_code VARCHAR(20) NOT NULL,
    dcls_month VARCHAR(6) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    dcls_chrg_man VARCHAR(255),
    homp_url VARCHAR(1024),
    cal_tel VARCHAR(100),
    financial_group_type VARCHAR(50) NOT NULL,
    source_payload TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_fin_company__company_code UNIQUE (financial_company_code)
);

CREATE TABLE IF NOT EXISTS financial_product (
    financial_product_id BIGSERIAL PRIMARY KEY,
    financial_company_id BIGINT NOT NULL REFERENCES financial_company(financial_company_id),
    financial_product_code VARCHAR(100) NOT NULL,
    financial_product_type VARCHAR(50) NOT NULL,
    dcls_month VARCHAR(6) NOT NULL,
    financial_product_name VARCHAR(255) NOT NULL,
    join_way TEXT,
    post_maturity_interest_rate TEXT,
    special_condition TEXT,
    join_restriction VARCHAR(50) NOT NULL,
    join_member TEXT NOT NULL,
    additional_notes TEXT NOT NULL,
    max_limit BIGINT,
    dcls_start_day DATE,
    dcls_end_day DATE,
    financial_submit_day TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL,
    last_seen_at TIMESTAMPTZ,
    product_content_hash VARCHAR(64),
    embedding_vector VECTOR(768),
    source_payload TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_fin_product__company_id_product_code_product_type UNIQUE (
        financial_company_id, financial_product_code, financial_product_type
    )
);

CREATE TABLE IF NOT EXISTS financial_product_option (
    financial_product_option_id BIGSERIAL PRIMARY KEY,
    financial_product_id BIGINT NOT NULL REFERENCES financial_product(financial_product_id),
    interest_rate_type VARCHAR(50) NOT NULL,
    reserve_type VARCHAR(50),
    deposit_period_months SMALLINT NOT NULL,
    base_interest_rate NUMERIC(8,5),
    maximum_interest_rate NUMERIC(8,5),
    source_payload TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS financial_product_history (
    observed_at TIMESTAMPTZ NOT NULL,
    financial_product_id BIGINT NOT NULL,
    financial_company_id BIGINT NOT NULL,
    financial_product_code VARCHAR(100) NOT NULL,
    financial_product_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    product_content_hash VARCHAR(64),
    payload JSONB NOT NULL,
    PRIMARY KEY (observed_at, financial_product_id)
);

CREATE INDEX IF NOT EXISTS ix_fin_product_history__product_id_observed_at_desc
    ON financial_product_history (financial_product_id, observed_at DESC);

CREATE TABLE IF NOT EXISTS financial_product_rate_history (
    observed_at TIMESTAMPTZ NOT NULL,
    financial_product_id BIGINT NOT NULL,
    financial_product_option_id BIGINT NOT NULL,
    interest_rate_type VARCHAR(50) NOT NULL,
    reserve_type VARCHAR(50),
    deposit_period_months SMALLINT NOT NULL,
    base_interest_rate NUMERIC(8,5),
    maximum_interest_rate NUMERIC(8,5),
    payload JSONB,
    PRIMARY KEY (observed_at, financial_product_id, financial_product_option_id)
);

CREATE INDEX IF NOT EXISTS ix_fin_product_rate_history__product_id_observed_at_desc
    ON financial_product_rate_history (financial_product_id, observed_at DESC);
