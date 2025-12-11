# Restaurant Management System (JDBC CRUD)

This is a Java program that performs CRUD (Create, Read, Update, Delete) operations on a MySQL database using JDBC and Prepared Statements. It is managed using **Maven**.

## Prerequisites

1. **Java Development Kit (JDK)**: Ensure Java (17 or later) is installed.
2. **Maven**: Ensure Maven is installed and available in your PATH.
3. **MySQL Server**: You need a running MySQL server.

## Setup

1. **Database Creation**:
   Login to your MySQL server and create the database:
   ```sql
   CREATE DATABASE restaurant_db;
   ```

2. **Configuration**:
   Open `src/main/java/RestaurantManager.java` and update the constants with your MySQL credentials:
   ```java
   private static final String USER = "root";
   private static final String PASSWORD = "your_password";
   ```

## Running the Application

This project uses Maven to handle dependencies (like the MySQL JDBC driver) automatically.

1. **Open the Terminal** in the project directory (`e:/JFS-42/task`).

2. **Run the Project**:
   Execute the following command:
   ```bash
   mvn clean compile exec:java
   ```

## Features

- **Insert Restaurant**: Add a new restaurant.
- **Read Restaurants**: List all restaurants.
- **Update Restaurant**: Update a restaurant's rating.
- **Delete Restaurant**: Remove a restaurant.
