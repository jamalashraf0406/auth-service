# Auth Service

## Features
- New User creation by admin user
- JWT token generation along with Refresh token
- JWT Token validation
- Refresh JWT token

## Tech Stack
- Java 17
- Spring Boot
- Spring security
- Spring Data JPA
- H2 database
- lombok

## Architecture
- Stateless microservices
- Restfull API for login, JWT generation and  validation
- H2 database dedicated to auth service for user credential and roles

## How to Run
1. Working on the docker compose for container deployment.
2. Can be run locally with below tools:
   - Java 17 
   - Maven 3.x
   - Spring boot 3
3. Once project downloaded in local and all toos install
   - mvn clean install
   - java -jar target/auth-service-0.0.1-SNAPSHOT.jar
4. Once Server up you can use the below API to test.
5. server url: http://localhost:8082

## Rest APIs 
- POST /auth/login
  - payload(default admin user): {
        "username": "jashraf07",
        "password": "password123"
    }
  - response: {
    "accessToken": "",
    "refreshToken": "",
    "token_type": "Bearer"
    }
- GET /auth/token/refresh
  - headers: 
    1. Authorization: bearer refresh-token
    2. X-Grant-Type: refresh_token
    3. response: {
      "accessToken": "",
      "tokenType": "Bearer"
    }
- if you want to validate request in api gateway then you can use public key api to fetch the public key by below endpoint
- GET /.well-known/jwks.json
    - Headers: 
      1. X-Gateway-Auth: gateway-secret-123
      2. response: {
         "keys": [
         { "kty": "RSA", "e": "AQAB",
         "kid": "3f7333ef-f337-4133-b414-1d6f0b87763d",
         "alg": "RS256", "n": ""
         }
         ]
         }
- POST /user/v1/create
  - Headers:
    1. Authorization: admin token to create normal user by default
    2. X-Gateway-Auth: gateway-secret-123
    3. Content-Type: application/json
  - payload: {"username":"newuser","password":"password123"}
  - response: {
        "message": "User created successfully!"
    }