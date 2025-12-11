import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class RestaurantManager {

    // Default connection settings
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "restaurant_db";
    private static final String PARAMS = "?allowPublicKeyRetrieval=true&useSSL=false";

    // Mutable credentials
    private static String USER = "root";
    private static String PASSWORD = "password";

    public static void main(String[] args) {
        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Please add the connector jar to your classpath.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        // 1. Establish Connection & Ensure DB Exists
        if (!setupDatabase(scanner)) {
            System.out.println("Could not establish database connection. Exiting.");
            return;
        }

        // 2. Ensure Table Exists
        createTable();

        // 3. Menu Loop
        while (true) {
            System.out.println("\n--- Restaurant Management System ---");
            System.out.println("1. Insert Restaurant");
            System.out.println("2. Read All Restaurants");
            System.out.println("3. Update Restaurant Rating");
            System.out.println("4. Delete Restaurant");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = -1;
            try {
                if (!scanner.hasNextLine())
                    break; // Prevent crash on EOF
                String input = scanner.nextLine();
                if (input.trim().isEmpty())
                    continue;
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    insertRestaurant(scanner);
                    break;
                case 2:
                    readRestaurants();
                    break;
                case 3:
                    updateRestaurant(scanner);
                    break;
                case 4:
                    deleteRestaurant(scanner);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static boolean setupDatabase(Scanner scanner) {
        boolean connected = false;
        while (!connected) {
            try (Connection conn = DriverManager.getConnection(BASE_URL + PARAMS, USER, PASSWORD);
                    Statement stmt = conn.createStatement()) {

                System.out.println("Connected to MySQL server successfully!");
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                System.out.println("Database '" + DB_NAME + "' checked/created.");
                connected = true;

            } catch (SQLException e) {
                System.out.println("\nFailed to connect to MySQL Server: " + e.getMessage());
                System.out.println("Please verify your MySQL password.");

                System.out.print("Do you want to enter new credentials? (y/n): ");
                if (!scanner.hasNextLine())
                    return false;
                String retry = scanner.nextLine().trim();
                if (!retry.equalsIgnoreCase("y")) {
                    return false;
                }

                System.out.print("Enter MySQL Username (default: root): ");
                if (scanner.hasNextLine()) {
                    String inputUser = scanner.nextLine().trim();
                    if (!inputUser.isEmpty())
                        USER = inputUser;
                }

                System.out.print("Enter MySQL Password: ");
                if (scanner.hasNextLine()) {
                    PASSWORD = scanner.nextLine().trim();
                }
            }
        }
        return true;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(BASE_URL + DB_NAME + PARAMS, USER, PASSWORD);
    }

    private static void executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Operation successful." : "No records found/updated.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS restaurant ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "type VARCHAR(50), "
                + "rating DOUBLE"
                + ")";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table 'restaurant' checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    private static void insertRestaurant(Scanner scanner) {
        System.out.print("Enter Restaurant Name: ");
        if (!scanner.hasNextLine())
            return;
        String name = scanner.nextLine();

        System.out.print("Enter Cuisine Type: ");
        if (!scanner.hasNextLine())
            return;
        String type = scanner.nextLine();

        double rating = -1;
        while (rating < 0 || rating > 5) {
            System.out.print("Enter Rating (0-5): ");
            try {
                if (!scanner.hasNextLine())
                    return;
                String input = scanner.nextLine().trim();
                if (input.isEmpty())
                    continue;
                rating = Double.parseDouble(input);
                if (rating < 0 || rating > 5)
                    System.out.println("Please enter a value between 0 and 5.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format.");
            }
        }

        String insertSQL = "INSERT INTO restaurant (name, type, rating) VALUES (?, ?, ?)";
        executeUpdate(insertSQL, name, type, rating);
    }

    private static void readRestaurants() {
        String selectSQL = "SELECT * FROM restaurant";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(selectSQL);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n--- Restaurant List ---");
            System.out.printf("%-5s %-20s %-15s %-5s%n", "ID", "Name", "Type", "Rating");
            System.out.println("------------------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                double rating = rs.getDouble("rating");
                System.out.printf("%-5d %-20s %-15s %-5.1f%n", id, name, type, rating);
            }
        } catch (SQLException e) {
            System.err.println("Error reading data: " + e.getMessage());
        }
    }

    private static void updateRestaurant(Scanner scanner) {
        int id = -1;
        while (id == -1) {
            System.out.print("Enter Restaurant ID to update: ");
            try {
                if (!scanner.hasNextLine())
                    return;
                String input = scanner.nextLine().trim();
                if (input.isEmpty())
                    continue;
                id = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID.");
            }
        }

        System.out.println("What do you want to update?");
        System.out.println("1. Name");
        System.out.println("2. Type");
        System.out.println("3. Rating");
        System.out.println("4. Cancel");
        System.out.print("Enter choice: ");

        int choice = -1;
        try {
            if (!scanner.hasNextLine())
                return;
            String input = scanner.nextLine().trim();
            if (!input.isEmpty())
                choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }

        switch (choice) {
            case 1:
                System.out.print("Enter New Name: ");
                if (!scanner.hasNextLine())
                    return;
                String name = scanner.nextLine();
                executeUpdate("UPDATE restaurant SET name = ? WHERE id = ?", name, id);
                break;
            case 2:
                System.out.print("Enter New Type: ");
                if (!scanner.hasNextLine())
                    return;
                String type = scanner.nextLine();
                executeUpdate("UPDATE restaurant SET type = ? WHERE id = ?", type, id);
                break;
            case 3:
                double rating = -1;
                while (rating < 0 || rating > 5) {
                    System.out.print("Enter New Rating (0-5): ");
                    try {
                        if (!scanner.hasNextLine())
                            return;
                        String input = scanner.nextLine().trim();
                        if (input.isEmpty())
                            continue;
                        rating = Double.parseDouble(input);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                }
                executeUpdate("UPDATE restaurant SET rating = ? WHERE id = ?", rating, id);
                break;
            case 4:
                System.out.println("Update cancelled.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void deleteRestaurant(Scanner scanner) {
        int id = -1;
        while (id == -1) {
            System.out.print("Enter Restaurant ID to delete: ");
            try {
                if (!scanner.hasNextLine())
                    return;
                String input = scanner.nextLine().trim();
                if (input.isEmpty())
                    continue;
                id = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID.");
            }
        }

        executeUpdate("DELETE FROM restaurant WHERE id = ?", id);
    }
}
