# ms-e-banking-ccv3x

E-banking microservices. Java 21, Spring Boot 4.1.1, Spring Cloud 2025.1.3, H2 (in-memory).

| Service | Port | Role |
|---|---|---|
| `ms-discovery` | 8761 | Eureka server: every service registers here |
| `ms-customer` | 8000 | Customers CRUD |
| `ms-account` | 8100 | Bank accounts; calls ms-customer through OpenFeign + Eureka |
| `ms-gateway` | 8888 | Spring Cloud Gateway: single entry point, static + dynamic routes resolved through Eureka |

## Run everything in Docker (recommended)

```bash
docker compose up --build -d     # builds the 4 images (Maven runs inside Docker, no local JDK needed)
docker compose logs -f ms-gateway
docker compose down
```

Eureka: http://localhost:8761 — Gateway: http://localhost:8888 — ms-customer is published on host port **8001** (8000 inside the container), ms-account on 8100.

## Gateway routing

| Kind | Gateway URL | Forwarded to |
|---|---|---|
| Static (`RouteLocator` bean) | `/customers/**` | `lb://MS-CUSTOMER` |
| Static | `/accounts/**` | `lb://MS-ACCOUNT` |
| Dynamic (`DiscoveryClientRouteDefinitionLocator`) | `/ms-customer/**` | `lb://MS-CUSTOMER` (prefix stripped) |
| Dynamic | `/ms-account/**` | `lb://MS-ACCOUNT` (prefix stripped) |

Dynamic routes are generated from the Eureka registry: any new service gets `/{service-id}/**` without touching the gateway.
List them at http://localhost:8888/actuator/gateway/routes.

## Postman

Import `ms-e-banking-ccv3x.postman_collection.json` (static routes, dynamic routes, direct calls, infrastructure).
Run it from the CLI: `docker run --rm --network host -v "$PWD":/etc/newman postman/newman:alpine run ms-e-banking-ccv3x.postman_collection.json`

## Build everything

```bash
mvn clean install          # from this folder: builds + tests the 4 services (18 tests)
```

## Run (in this order, one terminal each)

```bash
cd ms-discovery && ./mvnw spring-boot:run   # 1. wait for "Discovery server ... started"
cd ms-customer  && ./mvnw spring-boot:run   # 2.
cd ms-account   && ./mvnw spring-boot:run   # 3.
cd ms-gateway   && ./mvnw spring-boot:run   # 4.
```

Open the Eureka dashboard at http://localhost:8761: you should see **MS-CUSTOMER** and **MS-ACCOUNT** with status UP.

> Right after startup, `/accounts/{id}` can return **503 "Load balancer does not contain an instance for MS-CUSTOMER"** for up to ~30 s.
> That is normal: ms-account refreshes its copy of the Eureka registry every 30 s. Just retry.

H2 consoles: http://localhost:8000/h2-console (`jdbc:h2:mem:customerdb`) and http://localhost:8100/h2-console (`jdbc:h2:mem:accountsdb`), user `sa`, empty password.

## ms-customer endpoints

| Method | URL | Result |
|---|---|---|
| GET | `/customers` | 200 list / 404 if empty |
| POST | `/customers` | 201 / 400 if mobile exists or input invalid |
| GET | `/customers/{id}` | 200 / 404 |
| GET | `/customers/fetch?mobileNumber=...` | 200 / 404 |
| PUT | `/customers` (found by `mobileNumber`) | 200 / 404 |
| DELETE | `/customers/delete?mobileNumber=...` | 200 / 404 |

Validation on `CustomerDto`: names and email required (max 100), valid email, mobile number = 8 digits.

## ms-account endpoints

| Method | URL | Result |
|---|---|---|
| GET | `/accounts` | 200, all accounts (no call to ms-customer) |
| GET | `/accounts/{id}` | 200, account + its `customer` fetched from ms-customer / 404 |
| GET | `/accounts/customer/{customerId}` | 200, accounts of a customer / 404 if the customer doesn't exist |

If ms-customer is down or not yet registered, ms-account answers **503** with a clear message instead of a stack trace.

Example `GET /accounts/{id}`:

```json
{
  "accountId": "d38a0151-...", "balance": 84213.46, "createdAt": "2026-10-07",
  "currency": "TND", "type": "CURRENT", "customerId": 1,
  "customer": { "customerId": 1, "firstName": "Tarek", "lastName": "Zemzmi",
                "email": "tarek@zemzmi.com", "mobileNumber": "12345678" }
}
```

## How ms-account calls ms-customer

```java
@FeignClient(name = "MS-CUSTOMER")          // name registered in Eureka, no host/port hard-coded
public interface CustomerOpenFeign {
    @GetMapping("/customers/{id}")
    Customer fetchCustomerDetails(@PathVariable("id") Long id);
}
```

`@EnableFeignClients` on `MsAccountApplication` activates it. `Account.customer` is `@Transient`: only `customerId` is stored, and there is no foreign key between the two databases.

## Layout

```
ms-discovery/  MsDiscoveryApplication (@EnableEurekaServer)
ms-customer/   controllers, dtos, entities, enums, exceptions, mappers, repos, securities (empty), services(+impl)
ms-gateway/    MsGatewayserviceApplication (static RouteLocator + dynamic discovery routes)
ms-account/    controllers, dtos, entities, enums, exceptions, models (remote Customer), proxy (Feign), repos, services(+impl)
```
