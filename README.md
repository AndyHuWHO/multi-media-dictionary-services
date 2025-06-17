# Multimedia Dictionary Application

A reactive Spring Boot microservices application consisting of:
- Word Service: Manages dictionary entries with AI-powered definitions
- User Service: Handles user authentication and profiles

## Services

### Word Service

Manages dictionary words, definitions, and multimedia content.

**Environment Variables:**
- `spring.data.mongodb.uri`: MongoDB connection URI (required)
- `openai.api.key`: OpenAI API key (required)
- `openai.model`: OpenAI model name (optional, default: "gpt-4.1")

**Docker Deployment:**
```bash
docker build -t word-service .
docker run -p 8080:8080 \
  --env spring.data.mongodb.uri="mongodb://host:port/db" \
  --env openai.api.key="your-api-key" \
  --env openai.model="gpt-4.1" \
  word-service

### User service

Handles user authentication and authorization

**To run locally:**
- ./mvnw clean install
- ./mvnw spring-boot:run
