# How to Run the Smart Vehicle Procurement System

This guide explains how to run the application using PowerShell.

## Prerequisites

1.  **Java Development Kit (JDK) 17+**:
    - The application requires Java 17 or higher.
    - Check with: `java -version`

## Running the Application

We have consolidated everything into a single PowerShell script that handles setup, building, and running.

1.  Open **PowerShell**.
2.  Navigate to the project folder:
    ```powershell
    cd "c:\Users\DELL\project 2.0\Smart_Procurement_System-main"
    ```
3.  Run the start script:
    ```powershell
    .\run.ps1
    ```

The script will:

- Kill any existing Java processes to free up port 8080.
- Build the project using Maven.
- Start the application using the generated JAR file.
- The application will be available at: [http://localhost:8080](http://localhost:8080)

## Troubleshooting

- **Execution Policy Error**: If you see an error about scripts being disabled, run this command first:
  ```powershell
  Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
  ```
- **Port 8080 in use**: The script tries to handle this, but if it fails, manually check for other apps using port 8080.
