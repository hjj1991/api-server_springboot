CREATE EXTENSION IF NOT EXISTS timescaledb;
CREATE EXTENSION IF NOT EXISTS pgmq;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
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
    CONSTRAINT uq_financial_company_code UNIQUE (financial_company_code)
);

CREATE TABLE IF NOT EXISTS financial_company_area (
    financial_company_id BIGINT NOT NULL REFERENCES financial_company(financial_company_id),
    area_code VARCHAR(20) NOT NULL,
    area_name VARCHAR(255) NOT NULL,
    is_available BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (financial_company_id, area_code)
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
    CONSTRAINT uq_financial_product_natural UNIQUE (
        financial_company_id, financial_product_code, financial_product_type
    )
);

CREATE TABLE IF NOT EXISTS financial_product_option (
    financial_product_option_id BIGSERIAL PRIMARY KEY,
    financial_product_id BIGINT NOT NULL REFERENCES financial_product(financial_product_id),
    dcls_month VARCHAR(6) NOT NULL,
    interest_rate_type VARCHAR(50) NOT NULL,
    interest_rate_type_name VARCHAR(100),
    reserve_type VARCHAR(50),
    reserve_type_name VARCHAR(100),
    deposit_period_months SMALLINT NOT NULL,
    base_interest_rate NUMERIC(8,5),
    maximum_interest_rate NUMERIC(8,5),
    source_payload TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_financial_product_option_natural
        UNIQUE NULLS NOT DISTINCT (
            financial_product_id,
            interest_rate_type,
            reserve_type,
            deposit_period_months
        )
);

CREATE INDEX IF NOT EXISTS idx_financial_company_name_trgm
    ON financial_company USING gin (company_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_financial_product_active_seen
    ON financial_product (financial_product_type, status, last_seen_at);

CREATE INDEX IF NOT EXISTS idx_financial_product_fts
    ON financial_product USING gin (
        to_tsvector(
            'simple',
            coalesce(financial_product_name, '') || ' '
                    || coalesce(special_condition, '') || ' '
                    || coalesce(additional_notes, '')
        )
    );

CREATE INDEX IF NOT EXISTS idx_financial_product_option_rate
    ON financial_product_option (
        financial_product_id,
        deposit_period_months,
        interest_rate_type,
        reserve_type
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

SELECT create_hypertable(
    'financial_product_history',
    by_range('observed_at'),
    if_not_exists => TRUE
);

CREATE TABLE IF NOT EXISTS financial_product_rate_history (
    observed_at TIMESTAMPTZ NOT NULL,
    financial_product_id BIGINT NOT NULL,
    financial_product_option_id BIGINT,
    interest_rate_type VARCHAR(50) NOT NULL,
    reserve_type VARCHAR(50),
    deposit_period_months SMALLINT NOT NULL,
    base_interest_rate NUMERIC(8,5),
    maximum_interest_rate NUMERIC(8,5),
    payload JSONB,
    PRIMARY KEY (observed_at, financial_product_id, interest_rate_type, deposit_period_months)
);

SELECT create_hypertable(
    'financial_product_rate_history',
    by_range('observed_at'),
    if_not_exists => TRUE
);

CREATE INDEX IF NOT EXISTS idx_financial_product_history_payload
    ON financial_product_history USING gin (payload jsonb_path_ops);

CREATE INDEX IF NOT EXISTS idx_financial_product_rate_history_payload
    ON financial_product_rate_history USING gin (payload jsonb_path_ops);

SELECT add_retention_policy('financial_product_history', INTERVAL '365 days', if_not_exists => TRUE);
SELECT add_retention_policy('financial_product_rate_history', INTERVAL '365 days', if_not_exists => TRUE);

ALTER TABLE financial_product_history SET (
    timescaledb.compress,
    timescaledb.compress_orderby = 'observed_at DESC',
    timescaledb.compress_segmentby = 'financial_product_id'
);
SELECT add_compression_policy('financial_product_history', INTERVAL '30 days', if_not_exists => TRUE);

ALTER TABLE financial_product_rate_history SET (
    timescaledb.compress,
    timescaledb.compress_orderby = 'observed_at DESC',
    timescaledb.compress_segmentby = 'financial_product_id'
);
SELECT add_compression_policy('financial_product_rate_history', INTERVAL '30 days', if_not_exists => TRUE);

SELECT pgmq.create('product_change_events');

CREATE OR REPLACE FUNCTION enqueue_product_change_event(
    p_event_type TEXT,
    p_occurred_at TIMESTAMPTZ,
    p_financial_product_id BIGINT,
    p_financial_company_id BIGINT,
    p_financial_product_code TEXT,
    p_payload JSONB
) RETURNS SETOF BIGINT AS $$
    SELECT * FROM pgmq.send(
        'product_change_events',
        jsonb_build_object(
            'event_type', p_event_type,
            'occurred_at', p_occurred_at,
            'financial_product_id', p_financial_product_id,
            'financial_company_id', p_financial_company_id,
            'financial_product_code', p_financial_product_code,
            'payload', p_payload
        )
    );
$$ LANGUAGE sql;
