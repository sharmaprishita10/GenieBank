# GenieBank

GenieBank is a microservices project implemented in Java Spring Boot. It provides secure REST APIs for a bank’s net-banking features — customer onboarding, account management, transactions, authentication, and administrative operations — organized as independently deployable services with an API Gateway.

### Description

This project is a modular microservices implementation of a bank’s net-banking backend. The repository contains several services (authentication, customer, account, transaction, admin, and an API gateway) that together enable:

- Customer onboarding and profile management
- Account creation and balance inquiries
- Transaction processing (transfers, transaction history)
- Admin-level controls for bank personnel and teams
- Centralized authentication with JWT and role-based access control
- Gateway-based routing and request aggregation for client clients

The codebase demonstrates microservice boundaries, secure RESTful API design, separation of concerns, and best-practice backend patterns.

### Getting Started

APIs can be tested via the API Gateway at:  
`http://localhost:8080` (Gateway)  
Swagger UI (API documentation) is available at:  
`http://localhost:8080/swagger-ui/index.html`

### Specification

#### Authentication & Role Management (Authentication Service — Port 8082)
- Centralized authentication service manages `User`, `Role`, and `UserRoles`.
- Issues and validates JWT tokens.
- Exposes endpoints for sign-in, sign-out, and user creation (Admins call **Add User** to create employees/customers).
- Password management includes a change-password flow (validation of `newPassword` and `confirmPassword`).
- Roles: **Admin**, **Manager**, **Customer**.

#### API Gateway (Spring Cloud Gateway — Port 8080)
- Single entry point for external clients.
- Validates JWTs on incoming requests.
- All routes are secured by default except sign-in.
- Handles cross-cutting concerns: CORS, routing, header propagation, and basic request validation.

#### Admin Operations & Employee Management (Admin Service — Port 8081)
- Employee registration encodes passwords locally and internally calls Auth Service to create user credentials with the **Manager** role.
- OTP flows:
  - Send/verify mobile OTP via Twilio Verify.
  - Send/verify email OTP via Twilio + SendGrid dynamic templates.
- Customer registration workflow (performed by Admin/employee):
  - Verify mobile & email, validate age (>= 18), assign `branchId` = employee's branch id.
  - Generate `custId` using branch code. Customer Service returns the final `custId` (used as username).
  - Generate a random password and register the user via Auth Service.
- Account opening workflow:
  - Uses branch code as part of account number generation and delegates actual account creation to Account Service.
- Offline deposit & withdrawal flows:
  - **Deposit**: create transaction (Transaction Service) and update balance (Account Service).
  - **Withdrawal**: validate balance, create transaction, and update balance.
- Reporting helpers:
  - Get statement and balance for a customer by delegating to Customer → Account → Transaction services.
  - Fetch customer details by `custId`.

#### Customer Management (Customer Service — Port 8083)
- Netbanking flows:
  - Activate netbanking for a customer (enables online transfer).
  - **Transfer (online)**: validate netbanking status, check balances, orchestrate transaction creation and balance updates across Account and Transaction services.
- Get statement and balance by orchestrating account and transaction queries.
- View profile endpoint for customers.

#### Account Management (Account Service — Port 8084)
- Core flows:
  - Add account (called by Admin service when opening an account).
  - Update account balance (credit/debit operations) used by Transaction and Admin services.
  - Provide balance retrieval and account metadata.

#### Transaction Processing (Transaction Service — Port 8085) — Main Focus
- Critical flows:
  - **Add Transaction**: accepts requests from Admin (offline deposit/withdraw) and Customer (online transfer).
  - Ensure atomicity and ledger correctness: debit-credit pairing, rollback on failure, and clear transaction state management (`PENDING`, `SUCCESS`, `FAILED`).
  - Provide statement retrieval (paginated, filterable by date range) for reporting and customer/admin queries.
- Idempotency and validation mechanisms to prevent double-posting on retries.

#### Inter-service Communication & Integration
- Services interact via **OpenFeign** (service-to-service HTTP clients) and are routed through the API Gateway.
- Orchestrated flows call downstream services in sequence where eventual consistency is acceptable (e.g., create transaction → update balances).
- External integrations:
  - OTP via **Twilio Verify**
  - Emails via **SendGrid**

#### Security & Operational Concerns
- JWT-based stateless authentication issued by Auth Service; Gateway performs token validation and header injection.
- Role-based access enforcement implemented across services.

#### Observability, Validation & Error Handling
- Consistent error model across services (HTTP status codes + structured error body).
- Input validation at API boundaries with meaningful error messages for consumers.
- Health checks and readiness endpoints per service for orchestration and monitoring.

### About

GenieBank demonstrates a production-aligned microservices architecture using Java and Spring Boot, focusing on secure REST API design, inter-service orchestration with OpenFeign, centralized gateway concerns, and essential banking workflows (onboarding, account management, and transaction processing). The project highlights practical experience with authentication, role-based access control, and external integrations (Twilio/SendGrid).
