create table customers (
    id varchar(36) primary key,
    customer_id varchar(64) not null unique,
    first_name varchar(120) not null,
    last_name varchar(120) not null,
    gender varchar(16),
    date_of_birth date,
    age integer,
    email varchar(255),
    phone_number varchar(64),
    city varchar(120),
    state varchar(120),
    country varchar(32),
    postal_code varchar(32),
    occupation varchar(120),
    annual_income numeric(19,2),
    marital_status varchar(32),
    education_level varchar(64),
    employment_status varchar(64),
    customer_since date,
    customer_segment varchar(64),
    kyc_status varchar(32),
    risk_rating varchar(32),
    is_politically_exposed boolean,
    preferred_channel varchar(64),
    email_verified boolean,
    phone_verified boolean,
    num_complaints_last_year integer,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table accounts (
    id varchar(36) primary key,
    account_id varchar(64) not null unique,
    customer_id varchar(36) not null,
    account_type varchar(64) not null,
    account_status varchar(32) not null,
    currency varchar(8) not null,
    open_date date,
    close_date date,
    branch_code varchar(32),
    branch_city varchar(120),
    current_balance numeric(19,2),
    avg_monthly_balance_6m numeric(19,2),
    credit_limit numeric(19,2),
    credit_utilization_pct numeric(10,2),
    overdraft_enabled boolean,
    card_type varchar(32),
    is_joint_account boolean,
    num_linked_devices integer,
    mobile_banking_enrolled boolean,
    last_login_date date,
    avg_monthly_txn_count integer,
    account_tier varchar(32),
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_accounts_customer foreign key (customer_id) references customers (id)
);

create table exchange_rates (
    id varchar(36) primary key,
    from_currency varchar(8) not null,
    to_currency varchar(8) not null,
    rate numeric(19,6) not null,
    effective_date date not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table rule_configs (
    id varchar(36) primary key,
    config_key varchar(128) not null unique,
    enabled boolean not null,
    numeric_value numeric(19,6),
    text_value varchar(4000),
    description varchar(1000),
    created_at timestamp not null,
    updated_at timestamp not null
);

create table transactions (
    id varchar(36) primary key,
    transaction_id varchar(64) not null unique,
    account_id varchar(36) not null,
    amount numeric(19,2) not null,
    currency varchar(8) not null,
    normalized_amount_base numeric(19,2),
    counterparty varchar(255),
    channel varchar(64),
    jurisdiction varchar(128),
    transaction_timestamp timestamp not null,
    direction varchar(16) not null,
    transaction_type varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_transactions_account foreign key (account_id) references accounts (id)
);

create table alerts (
    id varchar(36) primary key,
    alert_key varchar(255) not null unique,
    customer_id varchar(64) not null,
    customer_name_masked varchar(255),
    account_id varchar(64),
    primary_rule varchar(64) not null,
    triggered_rules_csv varchar(1000) not null,
    evidence_csv varchar(4000) not null,
    explanation varchar(4000) not null,
    risk_score integer not null,
    status varchar(32) not null,
    disposition_reason varchar(1000),
    analyst_id varchar(128),
    case_id varchar(36),
    created_at timestamp not null,
    updated_at timestamp not null
);

create table case_files (
    id varchar(36) primary key,
    case_number varchar(64) not null unique,
    alert_id varchar(36) not null unique,
    customer_id varchar(64) not null,
    status varchar(32) not null,
    assigned_analyst varchar(128),
    disposition_reason varchar(1000),
    dispositioned_by varchar(128),
    dispositioned_at timestamp,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_case_alert foreign key (alert_id) references alerts (id)
);

create table audit_events (
    id varchar(36) primary key,
    entity_type varchar(128) not null,
    entity_id varchar(64) not null,
    action varchar(128) not null,
    actor_identity varchar(128) not null,
    details varchar(4000),
    created_at timestamp not null,
    updated_at timestamp not null
);
