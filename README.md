# 📚 Multimedia Dictionary Application

A reactive Spring Boot microservices application consisting of:

- **Word Service**: Manages dictionary entries with AI-powered definitions
- **User Service**: Handles user authentication and profiles

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

## 🔐 User Service

Handles user registration, authentication, role-based authorization, and profile management.

### ▶️ Local Run Instructions

```bash
./mvnw clean install
./mvnw spring-boot:run
```

**Base URL:** `/api/user-auth`

### 📘 Endpoints

#### `GET /api/user-auth`

**Description:**  
Returns a welcome message.

---

#### `POST /api/user-auth/register`

**Description:**  
Registers a new user.

---

#### `POST /api/user-auth/login`

**Description:**  
Authenticates a user and returns a JWT.

---

#### `POST /api/user-auth/get-membership`

**Description:**  
Upgrade user role to `ROLE_MEMBER` using the upgrade code `VIP123`.

**Authorization:**  
Requires Bearer JWT token.

---

#### `GET /api/user-auth/profile`

**Description:**  
Fetch the authenticated user's profile.

**Authorization:**  
Requires Bearer JWT token.

---

#### `GET /api/user-auth/user`

**Description:**  
Accessible only by users with role `USER`.

**Authorization:**  
Requires Bearer JWT token with role `USER`.

---

#### `GET /api/user-auth/member`

**Description:**  
Accessible only by users with role `MEMBER`.

**Authorization:**  
Requires Bearer JWT token with role `MEMBER`.

---


