# Backend Repo

This repository contains the RIAP backend application.

## Prerequisites

- Java 17 (JDK) and Apache Maven (`mvn`) available on your PATH.

If `mvn` is not available on Windows you'll see an error like:

```
mvn: The term 'mvn' is not recognized as a name of a cmdlet, function, script file, or executable program.
```

Install Maven (for example via winget) and re-run the commands below.

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
