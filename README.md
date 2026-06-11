# 🚀 Enterprise Order Broker System

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Spring Security](https://img.shields.io/badge/Spring%20Security-Enabled-success)
![Certification](https://img.shields.io/badge/VMware-Spring%20Certified%20Professional-blue)

> **A high-performance financial order processing engine.** > This project is not just a standard CRUD application. It was architected as an **elite software engineering laboratory** to consolidate and demonstrate the advanced architectural patterns required to achieve the **VMware Spring Certified Professional (2V0-72.22)** certification.

---

## 🎯 What Does This Project Demonstrate?

If you are a Tech Lead, Architect, or Tech Recruiter evaluating this repository, the code herein reflects a deep, practical mastery of the Spring ecosystem's inner workings, focusing heavily on critical enterprise concepts: resilience, security, and high-performance architecture.

### 🛠️ Architectural Highlights (Under the Hood)

* **AOP (Aspect-Oriented Programming) for Idempotency:** Implemented a transactional security net using a custom `@Idempotent` annotation. By leveraging *Spring AOP (CGLIB Proxies)*, incoming requests are intercepted to mitigate the risk of duplicate financial transactions without polluting the core business logic.
* **Advanced Transactional Boundaries:** Surgical management of database contexts using `@Transactional`. Practical exploration of complex propagation behaviors (`Propagation.REQUIRES_NEW` and `MANDATORY`) to ensure critical audit logs are persisted in strict isolation, preventing main-flow failures from corrupting historical data.
* **High-Performance Hybrid Data Access (JPA + JDBC):** * **Spring Data JPA** (Hibernate) handles the complex state consistency and lifecycle of business entities.
    * **Spring JDBC Template** with optimized `RowMapper` implementations is injected strategically for bulk read operations and analytical reporting, completely bypassing Hibernate's proxy and caching overhead for massive read scenarios.
* **Robust Method-Level Security (Spring Security):**
    * Modern, decoupled configuration using the `SecurityFilterChain` component.
    * Native customization of security failure points via `AuthenticationEntryPoint` (401 Unauthorized) and `AccessDeniedHandler` (403 Forbidden).
    * Method-level shielding using `@PreAuthorize` integrated with dynamic **SpEL (Spring Expression Language)** expressions, ensuring authenticated users can only mutate accounts strictly bound to their own requests.
* **Global Exception Handling & Web Resolutions:** Complete decoupling of error flows from Controllers using a global `@RestControllerAdvice` interceptor. It captures and standardizes validation exceptions (`MethodArgumentNotValidException`) and domain failures, converting them into clean JSON payloads compliant with the RFC 7807 specification.
* **Infrastructure Telemetry with Actuator:** Extended application vital signs through a custom `HealthIndicator`. This component dynamically evaluates the operational status of external integrations (e.g., B3 Stock Exchange), altering the global application state to `OUT_OF_SERVICE` if latency thresholds or operational hours are violated.
* **Test Slicing Strategy:** Highly efficient automated test coverage. Advanced use of `@WebMvcTest` in synergy with explicit security `@Import` and `@WithMockUser` to isolate and simulate HTTP calls via `MockMvc`, ensuring fast assertions without the overhead of initializing the database or cloud infrastructure contexts.

---

## 💻 Tech Stack

| Layer | Technology / Pattern |
| :--- | :--- |
| **Core Engine** | Java 17, Spring Core (IoC Context, Dependency Injection) |
| **Web Layer** | Spring Web MVC, RESTful APIs, DispatcherServlet Flow |
| **Security** | Spring Security, Cryptography (BCrypt), Custom Security Filters |
| **Persistence** | Spring Data JPA, Spring JDBC, H2 Database (In-Memory Testing) |
| **Integrations** | RestTemplate (Custom configurations with precise read/connect timeouts) |
| **Ops & Config** | Spring Boot Actuator, Type-safe Metadata via `@ConfigurationProperties` |
| **Testing Suite** | JUnit 5, Mockito Framework, MockMvc Test Slices |

---

## ⚙️ How to Run

**Prerequisites:** Java 17 and Maven installed locally.

1. Clone the repository: `git clone https://github.com/YOUR-USERNAME/enterprise-order-broker.git`
2. Navigate to the project directory: `cd enterprise-order-broker`
3. Build and run the application: `./mvnw spring-boot:run`
4. Monitor system health via Actuator: Open `http://localhost:8080/actuator/health` in your browser.

---

## 📡 Main Endpoints

The API is exposed under the `/api/v1/orders` prefix. For write/delete operations, include the appropriate authentication credentials in the HTTP header.

* `POST /api/v1/orders` - Registers a new financial order (Requires Authentication & SpEL account validation).
* `GET /api/v1/orders/{id}` - Retrieves the detailed, updated state of a specific order.
* `DELETE /api/v1/orders/{id}` - Manually cancels an order (Restricted to `ADMIN` role).
* `GET /api/public/status` - Public heartbeat route for external monitoring.

---

## 👨‍💻 About the Author

**Luan Alves Genro** Software Engineer & IT Specialist focused on designing robust backend architectures. This project reflects the practical application and technical consolidation of the advanced studies that led to achieving the official **VMware Spring Certified Professional** certification.

* **LinkedIn:** [View Professional Profile](https://www.linkedin.com/in/luan-alves-genro-821aa5182/)
* **VMware Credential:** [View Official Certification Badge](https://www.credly.com/badges/075c864c-195e-4332-b613-6fba4fb8b001/public_url)

> *"Building software is not just about making the code compile; it's about deeply understanding the engine that makes it run."*