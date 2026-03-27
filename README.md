## Car Rental CI/CD

This repository contains a Spring Boot microservice and a complete Jenkins‑based CI/CD pipeline.  
The goal is to demonstrate automated build, testing, code quality analysis, Docker image creation, and deployment to a local Docker host.

### Features
- Spring Boot microservice (Cars & Bookings domain)
- Full test suite (unit, slice, integration, E2E)
- Jenkins declarative pipeline
- SonarQube static analysis with quality gates
- Docker image build and automated deployment

### Project Structure
- `src/` — application source code and tests  
- `pom.xml` — Maven build configuration  
- `Dockerfile` — container build definition  
- `Jenkinsfile` — CI/CD pipeline  
- `deploy.sh` — simple deployment script  

### How to Run Locally
```bash
mvn clean spring-boot:run
