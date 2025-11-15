## Project Overview
This project is built using Java 17 and Spring Boot, with Maven as the build tool. It also uses an H2 in-memory database for temporary data storage during log analysis.
This project is a log file analyser that processes one or more uploaded `.log` files and produces the following statistics:

- Count the number of `LOGIN_SUCCESS` and `LOGIN_FAILURE` events per user.
- Identify the top 3 users with the most `FILE_UPLOAD` events.
- Detect suspicious activity: more than 3 `LOGIN_FAILURE` attempts from the same IP address within a 5-minute window.

The results are available either as a JSON API response or as a downloadable `result.json` file.

## Maven Build Instructions
```
mvn clean test compile package
cd target
java -jar spring-boot-0.0.1-SNAPSHOT.jar
```

## Docker Build Instructions
```
docker build -t frei-assessment .
docker run -p 8080:8080 --name frei-assessment frei-assessment
docker stop frei-assessment
```

## Online Version
- https://technical-assessment-k48u.onrender.com/swagger-ui/index.html
- https://technical-assessment-k48u.onrender.com/h2-console

### H2 Database (Online)
- JDBC URL: `jdbc:h2:mem:logsdb`
- User: `log-analyser`
- Password: *(leave empty)*

## Local Access
### Swagger UI
http://localhost:8080/swagger-ui.html

### H2 Console
http://localhost:8080/h2-console

### H2 Database (Local)
- JDBC URL: `jdbc:h2:mem:logsdb`
- User: `log-analyser`
- Password: *(leave empty)*

## Author
Hugo van Bart

