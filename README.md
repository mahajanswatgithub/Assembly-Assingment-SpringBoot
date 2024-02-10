# Assembly Language Simulator with Spring Boot and Redis Integration

This project is a Spring Boot application that simulates an assembly language execution. It allows users to interactively enter commands and perform operations on registers. The application includes an endpoint for accepting assembly programs, executing them, and saving the results in Redis.

## Technical Stack Used

- Java 8
- Spring Boot
- Redis
- Docker
- Minikube (Optional)
- IntelliJ IDEA

## Getting Started

1. Clone the repository or download the source code files.
2. Open the project in your preferred Java development environment (e.g., IntelliJ IDEA).
3. Decide how you want to run the project:

   - **Option 1: Docker**

     - Build the Docker image for the Spring Boot application.
     - Run the Docker container.

       Example commands:

       ```bash
       docker build -t miko-assignment-sb-docker1:latest .
       docker run -p 8080:8080 miko-assignment-sb-docker1:latest
       ```

   - **Option 2: Minikube (Optional)**

     - Ensure that Minikube is installed.
     - Start Minikube.
     - Deploy the Spring Boot application in the Minikube cluster.

       Example commands:

       ```bash
       minikube start
       kubectl apply -f minikube.yaml
       ```

## Testing with Postman

1. Ensure that the Spring Boot application is running.
2. Open Postman or any API testing tool of your choice.
3. Send a POST request to `http://localhost:8080/api/assembly-program/execute` with the assembly program as the request body.

   Example using Postman:

   - Method: POST
   - URL: `http://localhost:8080/api/assembly-program/execute`
   - Body: Raw (Text)
     ```
     { "1": "MV 1,#500", "2": "MV 2,#200", "3": "ADD 1,2" }
     ```

4. The program will execute the provided assembly program, save the results in Redis, and return success or failure.

Note: In this simplified version, users can call the `http://localhost:8080/api/assembly-program/execute/execute` API with a JSON payload containing a map of integer to string values representing the assembly program. The response will indicate success or failure.

##

Happy Coding !
