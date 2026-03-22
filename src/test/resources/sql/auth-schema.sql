create table if not exists users (
    id bigserial primary key,
    display_name varchar(100) not null,
    email_ciphertext text null,
    email_lookup_hash varchar(64) null,
    email_verified_at timestamptz null,
    token_version bigint not null default 0,
    status varchar(32) not null,
    created_at timestamptz not null default now(),
    modified_at timestamptz not null default now(),
    constraint uq_users__email_lookup_hash unique (email_lookup_hash)
);

create index if not exists ix_users__status
    on users (status);

create table if not exists auth_identities (
    id bigserial primary key,
    user_id bigint not null,
    provider_type varchar(20) not null,
    login_id varchar(50) null,
    provider_subject_hash varchar(64) not null,
    status varchar(32) not null,
    linked_at timestamptz not null,
    last_login_at timestamptz null,
    created_at timestamptz not null default now(),
    modified_at timestamptz not null default now(),
    constraint uq_auth_identities__provider_type_subject_hash
        unique (provider_type, provider_subject_hash),
    constraint uq_auth_identities__provider_type_login_id
        unique (provider_type, login_id),
    constraint chk_auth_identities__local_login_id_required
        check (provider_type <> 'LOCAL' or login_id is not null),
    constraint fk_auth_identities__user
        foreign key (user_id) references users (id) on delete cascade
);

create index if not exists ix_auth_identities__user_id_provider_type
    on auth_identities (user_id, provider_type);

create table if not exists local_credentials (
    id bigserial primary key,
    user_id bigint not null,
    password_hash varchar(255) not null,
    password_algo varchar(50) not null,
    password_updated_at timestamptz not null,
    failed_attempt_count integer not null default 0,
    locked_until timestamptz null,
    created_at timestamptz not null default now(),
    modified_at timestamptz not null default now(),
    constraint uq_local_credentials__user_id unique (user_id),
    constraint fk_local_credentials__user
        foreign key (user_id) references users (id) on delete cascade
);

create table if not exists roles (
    id bigserial primary key,
    role_name varchar(30) not null,
    created_at timestamptz not null default now(),
    modified_at timestamptz not null default now(),
    constraint uq_roles__role_name unique (role_name)
);

create table if not exists user_roles (
    id bigserial primary key,
    user_id bigint not null,
    role_id bigint not null,
    created_at timestamptz not null default now(),
    modified_at timestamptz not null default now(),
    constraint uq_user_roles__user_id_role_id unique (user_id, role_id),
    constraint fk_user_roles__user
        foreign key (user_id) references users (id) on delete cascade,
    constraint fk_user_roles__role
        foreign key (role_id) references roles (id) on delete cascade
);

create index if not exists ix_user_roles__role_id
    on user_roles (role_id);

create table if not exists user_deletion_requests (
    id bigserial primary key,
    user_id bigint not null,
    status varchar(20) not null,
    requested_at timestamptz not null,
    scheduled_purge_at timestamptz not null,
    cancelled_at timestamptz null,
    purged_at timestamptz null,
    created_at timestamptz not null default now(),
    modified_at timestamptz not null default now()
);

create index if not exists ix_user_deletion_requests__user_id_requested_at
    on user_deletion_requests (user_id, requested_at);

create index if not exists ix_user_deletion_requests__status_scheduled_purge_at
    on user_deletion_requests (status, scheduled_purge_at);

create unique index if not exists uq_user_deletion_requests__user_id_pending
    on user_deletion_requests (user_id)
    where status = 'PENDING';
