# Dynamic Dataset API

A Spring Boot backend application to store dynamic JSON records in a relational database (MySQL) and query them using `groupBy` and `sortBy`.

## Tech Stack
- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Jackson (`ObjectMapper`)
- Lombok

## Database Setup & Connection

1. Open MySQL (MySQL Workbench or MySQL CLI) and create the database:
   ```sql
   CREATE DATABASE dataset_db;
   ```

2. Open `src/main/resources/application.properties` and verify the datasource configuration:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/dataset_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=${DB_PASSWORD:your_password_here}
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

3. Connect your MySQL password safely via an environment variable (never commit your real password to GitHub):

   **Windows (PowerShell):**
   ```powershell
   $env:DB_PASSWORD="your_mysql_password
   ```

   **Windows (CMD):**
   ```cmd
   set DB_PASSWORD=your_mysql_password
   ```

   **Linux / macOS / Git Bash:**
   ```bash
   export DB_PASSWORD="your_mysql_password"
   ```

## How to Run Locally

1. Open a terminal and move into the project root directory (where `pom.xml`, `mvnw`, and `mvnw.cmd` are located):
   ```bash
   cd path/to/your-project-folder
   ```

2. Start the Spring Boot server (runs on `http://localhost:8080` and automatically creates/updates the `dataset_records` table):

   **Windows (PowerShell):**
   ```powershell
   $env:DB_PASSWORD="your_mysql_password"
   .\mvnw.cmd spring-boot:run
   ```

   **Windows (CMD):**
   ```cmd
   set DB_PASSWORD=your_mysql_password
   mvnw.cmd spring-boot:run
   ```

   **Linux / macOS / Git Bash:**
   ```bash
   export DB_PASSWORD="your_mysql_password"
   ./mvnw spring-boot:run
   ```

## API Endpoints

### 1. Insert Record
- **Method & URL:** `POST /api/dataset/{datasetName}/record`
- **Headers:** `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "id": 1,
    "name": "peter parker",
    "age": 30,
    "department": "Engineering"
  }
  ```
- **Response:**
  ```json
  {
    "message": "Record added successfully",
    "dataset": "employee_dataset",
    "recordId": 1
  }
  ```

### 2. Query Records (Group By)
- **Method & URL:** `GET /api/dataset/{datasetName}/query?groupBy=department`
- **Response:**
  ```json
  {
    "groupedRecords": {
      "Engineering": [
        {
          "id": 1,
          "name": "peter parker",
          "age": 30,
          "department": "Engineering"
        },
        {
          "id": 2,
          "name": "gwen staccy",
          "age": 25,
          "department": "Engineering"
        }
      ]
    }
  }
  ```

### 3. Query Records (Sort By)
- **Method & URL:** `GET /api/dataset/{datasetName}/query?sortBy=age&order=asc`
- **Response:**
  ```json
  {
    "sortedRecords": [
      {
        "id": 2,
        "name": "gwen staccy",
        "age": 25,
        "department": "Engineering"
      },
      {
        "id": 1,
        "name": "peter parker",
        "age": 30,
        "department": "Engineering"
      }
    ]
  }
  ```

## Running Tests
```bash
./mvnw test
```