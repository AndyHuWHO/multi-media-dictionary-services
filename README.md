Reactive Springboot micro-services for a multi-media dictionary application

Word-service:
  env vars needed: 1. spring.data.mongodb.uri 2. openai.api.key  3. openai.model (optional, default is gpt-4.1)
  dockerfile available, to run in docker:
  docker build -t your-app-name .
  docker run -p 8080:8080 \
  --env spring.data.mongodb.uri="mongodb://host:port/db" \
  --env openai.api.key="your-api-key" \
  --env openai.model="gpt-4.1" \
  your-app-name




User-service: 
    to run:  ./mvnw clean install        
    then   ./mvnw spring-boot:run
