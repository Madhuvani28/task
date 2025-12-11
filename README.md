# Restaurant Management System (Standalone)

This project is a Java-based Restaurant Management System that connects to a MySQL database. It is built with Maven and includes all necessary dependencies in a single executable JAR file.

## Prerequisites

1.  **Java Runtime Environment (JRE) or JDK**: Version 17 or higher.
2.  **MySQL Server**: Ensure your MySQL server is running.
    *   The app will attempt to connect with user `root` and password `password`.
    *   If that fails, it will **prompt you** to enter your own credentials.
    *   It will automatically create the `restaurant_db` database if it doesn't exist.

## How to Build (Optional)

If you have the source code and Maven installed, you can rebuild the project:

```bash
mvn clean package
```

This generates `target/restaurant-manager-1.0-SNAPSHOT.jar`.

## How to Run

You can run the application directly using the JAR file. No external classpath setup is needed.

**Run command:**

```bash
java -jar target/restaurant-manager-1.0-SNAPSHOT.jar
```

## Features

*   **Insert Restaurant**: Add Name, Cuisine Type, and Rating.
*   **Read Restaurants**: View all stored restaurants.
*   **Update Restaurant**: Modify specific fields (Name, Type, Rating) by ID.
*   **Delete Restaurant**: Remove a restaurant by ID.
*   **Auto-Configuration**: Automatically prompts for DB credentials if default ones fail.
