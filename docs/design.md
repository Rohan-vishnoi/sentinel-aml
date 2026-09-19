# Sentinel AML Design Document

## 1. High-Level Design (HLD)

### 1.1 Goal
Build a Spring Boot AML monitoring platform that ingests customer, account, and transaction data, applies configurable detection rules, creates explainable alerts, and supports analyst case handling with audit history.

### 1.2 System overview
```text
Client/UI/API
   |
   v
Controllers (REST + Dashboard)
   |
   v
Services (Ingestion, Detection, Alert, Case, Rules, Audit)
   |
   v
Repositories (Spring Data JPA)
   |
   v
Relational Database (H2 for local demo, PostgreSQL profile supported)
```

### 1.3 Main components
| Component | Responsibility |
|---|---|
| Ingestion API | Accepts bulk and single transaction/customer/account payloads |
| Detection engine | Evaluates AML typologies and creates alert candidates |
| Alert service | Deduplicates alerts, masks PII, stores risk score and evidence |
| Case service | Opens cases and tracks disposition |
| Rule config service | Loads tunable thresholds and enable flags from DB |
| Currency conversion service | Normalizes amounts to base currency |
| Audit service | Persists immutable event history |
| Dashboard | Shows alert queue and cases for analysts |

### 1.4 Key workflows
#### A. Ingestion and detection
1. Customer/account seed data is loaded at startup.
2. A transaction is saved through `/api/v1/transactions` or bulk ingestion.
3. The transaction is normalized to the base currency.
4. The detection engine evaluates all enabled rules.
5. Matching rules produce a candidate alert.
6. Alert service upserts the alert and opens/updates a related case.
7. Audit entries are written for traceability.

#### B. Analyst triage
1. Analyst opens `/dashboard` or `/api/v1/alerts`.
2. The alert list is sorted by risk score descending.
3. Analyst opens a case and sets disposition.
4. The case and alert state changes are recorded in audit history.

#### C. Rule tuning
1. Admin updates `rule_configs` through `/api/v1/admin/rules`.
2. Detection logic reads the latest values from the database.
3. New transactions are evaluated using updated thresholds without redeploying.

### 1.5 Data storage
The application uses a relational model with these core tables:
- `customers`
- `accounts`
- `transactions`
- `alerts`
- `case_files`
- `rule_configs`
- `exchange_rates`
- `audit_events`

### 1.6 Security
- HTTP Basic auth is used for demo simplicity.
- Roles: `ADMIN`, `ANALYST`
- PII is masked in list views.
- Sensitive admin endpoints are restricted to admin users.

### 1.7 Deployment model
- Local dev: H2 in-memory database
- Demo/prod-style setup: PostgreSQL via `application-postgres.yml`
- Schema is managed through Flyway migrations

## 2. Low-Level Design (LLD)

### 2.1 Package structure
| Package | Purpose |
|---|---|
| `controller` | REST and dashboard entry points |
| `service` | Business logic and AML rules |
| `repository` | Spring Data JPA persistence layer |
| `domain` | JPA entities and enums |
| `dto` | Request/response records |
| `config` | Security and properties binding |
| `exception` | Application-specific errors and handler |

### 2.2 Entity model
#### Customer
- Stores KYC, profile, and risk data.
- One customer has many accounts.

#### Account
- Belongs to one customer.
- Stores account metadata and balances.

#### TransactionRecord
- Belongs to one account.
- Stores amount, currency, direction, jurisdiction, and normalized amount.

#### Alert
- One alert per detected pattern key.
- Stores risk score, explanation, evidence, and masking.

#### CaseFile
- One case is linked to one alert.
- Stores analyst disposition and status.

#### RuleConfig
- Stores tunable values for AML thresholds and toggles.

#### ExchangeRate
- Stores conversion rates to the configured base currency.

#### AuditEvent
- Records immutable entity actions with actor identity.

### 2.3 Service logic
#### IngestionService
- Validates account/customer existence.
- Persists incoming records.
- Normalizes transaction amounts.
- Triggers detection after transaction ingest.

#### DetectionService
Evaluates these rules:
- CTR threshold
- Structuring
- Rapid movement
- High-risk jurisdiction
- Behavioral deviation
- Round-number pattern

Rule outputs are converted into `DetectedAlert` candidates with:
- alert key
- customer/account IDs
- primary rule
- triggered rule list
- evidence transaction IDs
- explanation
- risk score

#### AlertService
- Upserts alerts by `alertKey`
- Masks customer name
- Creates or updates case records
- Writes audit history

#### CaseService
- Lists cases
- Opens cases for alerts
- Updates disposition state

#### RuleConfigService
- Reads rule config values from DB
- Supplies defaults when a rule row is missing

#### CurrencyConversionService
- Converts a transaction amount to the base currency
- Uses the latest effective exchange rate on or before the transaction date

#### AuditService
- Stores every important state change in `audit_events`

### 2.4 REST API design
| Method | Path | Purpose |
|---|---|---|
| POST | `/api/v1/transactions` | Stream one transaction |
| POST | `/api/v1/ingestion/customers` | Bulk customer ingest |
| POST | `/api/v1/ingestion/accounts` | Bulk account ingest |
| POST | `/api/v1/ingestion/transactions` | Bulk transaction ingest |
| GET | `/api/v1/alerts` | Alert queue |
| GET | `/api/v1/alerts/{id}` | Alert details |
| PUT | `/api/v1/alerts/{id}/disposition` | Update alert disposition |
| GET | `/api/v1/cases` | Case list |
| GET | `/api/v1/cases/{caseNumber}` | Case details |
| POST | `/api/v1/cases/alerts/{alertId}` | Open case for alert |
| PUT | `/api/v1/cases/{caseNumber}/disposition` | Disposition case |
| GET | `/api/v1/admin/rules` | List rule configs |
| POST | `/api/v1/admin/rules` | Create rule config |
| PUT | `/api/v1/admin/rules/{configKey}` | Update rule config |

### 2.5 Alert scoring
Risk score is derived from triggered rule severity:
- High-risk jurisdiction: 95
- Rapid movement: 90
- Structuring: 85
- Behavioral deviation: 75
- CTR threshold: 70
- Round-number pattern: 40

### 2.6 Deduplication strategy
- `alertKey` is the deduplication key.
- Repeated matches for the same customer/pattern/time bucket update the same alert instead of creating duplicates.

### 2.7 Error handling
- `NotFoundException` returns 404
- `BadRequestException` returns 400
- Validation and persistence issues return structured JSON error bodies

### 2.8 Demo data
Startup seed data includes:
- synthetic customers
- accounts
- exchange rates
- rule config rows
- suspicious transactions covering multiple typologies

### 2.9 Extension points
The design leaves room for:
- Kafka-based streaming ingestion
- ML anomaly scoring
- rule versioning
- SAR draft generation
- richer analyst dashboards

## 3. Summary
The platform is a layered Spring Boot application with JPA persistence, configurable rule-based AML detection, analyst workflow support, and audit-first state management.
