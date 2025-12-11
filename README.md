# Menu Management System

This is a **Menu Management System** (formerly Restaurant Manager) built in Java. It allows you to manage a menu database (MySQL) with operations to Add, View, Update, and Delete items.

It is built as a standalone executable JAR using Maven.

## Prerequisites

1.  **Java Runtime Environment (JRE) or JDK**: Version 17+.
2.  **MySQL Server**: Running on localhost port 3306.
    *   The app will try to connect with `root` / `password`.
    *   If that fails, it will ask for your credentials.
    *   It automatically creates the database `menu_db` and table `menu_items`.

## How to Run

You can run the application directly:

```bash
java -jar target/restaurant-manager-1.0-SNAPSHOT.jar
```

*Note: The jar name might still be `restaurant-manager` unless you change the artifactId in pom.xml, but the functionality inside is the new Menu Manager.*

## Features

*   **Add Item**: Input Item Name and Price.
*   **View Items**: Shows Serial Number, Name, and Price.
*   **Update Item**: Update Name or Price by Serial Number.
*   **Delete Item**: Remove an item by Serial Number.
