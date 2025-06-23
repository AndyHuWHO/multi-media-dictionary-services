# 📚 Multimedia Dictionary Application

A reactive Spring Boot microservices application consisting of:

- **Word Service**: Manages dictionary entries with AI-powered definitions
- **Auth Service**: Handles user authentication and authorization

---

## 🧠 Word Service

Provides dictionary word definitions powered by a large language model (LLM).

### 🔧 Environment Variables

- `spring.data.mongodb.uri`: MongoDB connection URI (**required**)
- `openai.api.key`: OpenAI API key (**required**)
- `openai.model`: OpenAI model name (optional, default: `"gpt-4.1"`)

### 🐳 Docker Deployment

```bash
docker build -t word-service .
docker run -p 8080:8080 \
  --env spring.data.mongodb.uri="mongodb://host:port/db" \
  --env openai.api.key="your-api-key" \
  --env openai.model="gpt-4.1" \
  word-service
```

### 📘 Endpoints

#### `GET /api/words/{word}`

**Description:**  
Retrieve dictionary information for the specified word.

---

## 🔐 Auth Service

Handles user registration, authentication, role-based authorization, and user-role management.

### ▶️ Local Run Instructions

```bash
./mvnw clean install
./mvnw spring-boot:run
```

**Base URL:** `/api/user-auth`

### 📘 Endpoints

#### `GET /api/auth`
**Description:**  
Returns a welcome message.

---

#### `POST /api/auth/register`
**Description:**  
Registers a new user.  
**Request Body:**  
```json
{
  "email": "user@example.com",
  "password": "yourPassword",
  "authProvider": "REGISTRATION"
}
```
**Response:**  
Returns user public ID, email, role, and creation timestamp.

---

#### `POST /api/auth/login`
**Description:**  
Authenticates a user and returns a JWT.  
**Request Body:**  
```json
{
  "email": "user@example.com",
  "password": "yourPassword"
}
```
**Response:**  
Returns user public ID, email, role, and JWT token.

---

#### `POST /api/auth/get-member`
**Description:**  
Upgrade user role to `MEMBER` using an upgrade code.  
**Request Body:**  
```json
{
  "upgradeCode": "VIP123"
}
```
**Authorization:**  
Requires Bearer JWT token.  
**Response:**  
Returns updated user info and new JWT token.

---

#### `GET /api/auth/profile`
**Description:**  
Fetch the authenticated user's profile.  
**Authorization:**  
Requires Bearer JWT token.

---

#### `GET /api/auth/user`
**Description:**  
Accessible only by users with role `USER`.  
**Authorization:**  
Requires Bearer JWT token with role `USER`.

---

#### `GET /api/auth/member`
**Description:**  
Accessible only by users with role `MEMBER`.  
**Authorization:**  
Requires Bearer JWT token with role `MEMBER`.

---



