# Remitly Stock Market Simulator

A simplified, high-availability stock market simulation engine developed as a RESTful service.

## Architecture & Features

- **High Availability**: The system is deployed with two application instances behind an Nginx load balancer to ensure service continuity.
- **Resilience**: Supports a "Chaos" endpoint that simulates instance failure; the system remains operational via the load balancer.
- **Persistence**: Uses PostgreSQL as the underlying database, orchestrated via Docker.
- **Auditability**: Tracks all successful buy/sell operations in an audit log.

## Prerequisites

- [Docker](https://www.docker.com/) (with Docker Compose)
- [Java 21](https://adoptium.net/) (for local builds)
- [Maven](https://maven.apache.org/)

## Quick Start

1. **Build the project**:
   ```bash
   mvn clean package
   Launch the infrastructure:

Bash
docker-compose up --build
The API will be available at: http://localhost:8080

API Endpoints
POST /stocks - Set the bank's stock inventory.

GET /stocks - View current bank stock availability.

POST /wallets/{wallet_id}/stocks/{stock_name} - Buy or sell a single stock ({"type": "buy"} or {"type": "sell"}).

GET /wallets/{wallet_id} - Get the wallet's current state.

GET /wallets/{wallet_id}/stocks/{stock_name} - Get quantity of a specific stock in a wallet.

GET /log - Retrieve the audit log of all successful operations.

POST /chaos - Simulates a service failure by terminating the instance.

Engineering Practices
Transactions: All critical operations (buying/selling) are annotated with @Transactional to ensure data consistency.

Dependency Injection: Spring Boot's IoC container is used for clean, testable component management.

Containerization: The entire stack is containerized for cross-platform portability.
