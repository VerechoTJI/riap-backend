# Backend Repo

This repository contains the RIAP backend application.

## Prerequisites

- Java 17 (JDK) and Apache Maven (`mvn`) available on your PATH.

If `mvn` is not available on Windows you'll see an error like:

```
mvn: The term 'mvn' is not recognized as a name of a cmdlet, function, script file, or executable program.
```

Install Maven (for example via winget) and re-run the commands below.

## Run the Application

To run the backend server with the actual PostgreSQL database, first start the database from the root repository folder:

```powershell
docker-compose up -d
```

Then, set the environment variables and run with the `postgres` profile:

```powershell
$env:DB_PORT="5433"
$env:DB_PASSWORD="riap"
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

If you just want to run with an in-memory H2 database (data will be lost on restart), simply run:

```powershell
mvn spring-boot:run
```

## Build

From this folder:

```powershell
mvn test
```

To create a packaged JAR (runs tests by default):

```powershell
mvn package
```

To package while skipping tests (faster iteration):

```powershell
mvn package -DskipTests
```

## Layout

- `src/main/java`: backend source code.
- `src/test/java`: backend tests.
