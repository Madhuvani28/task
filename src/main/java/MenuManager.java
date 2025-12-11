import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class MenuManager {

    // Default connection settings
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "menu_db"; // Changed DB name for clarity
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
            System.out.println("\n--- Menu Management System ---");
            System.out.println("1. Add Menu Item");
            System.out.println("2. View All Items");
            System.out.println("3. Update Menu Item");
            System.out.println("4. Delete Menu Item");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = -1;
            try {
                if (!scanner.hasNextLine())
                    break;
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
                    addItem(scanner);
                    break;
                case 2:
                    viewItems();
                    break;
                case 3:
                    updateItem(scanner);
                    break;
                case 4:
                    deleteItem(scanner);
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
        // Table: menu_items
        // Columns: serial_number (PK), item_name, price
        String createTableSQL = "CREATE TABLE IF NOT EXISTS menu_items ("
                + "serial_number INT AUTO_INCREMENT PRIMARY KEY, "
                + "item_name VARCHAR(100) NOT NULL, "
                + "price DOUBLE NOT NULL"
                + ")";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table 'menu_items' checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    private static void addItem(Scanner scanner) {
        System.out.print("Enter Item Name: ");
        if (!scanner.hasNextLine())
            return;
        String name = scanner.nextLine();

        double price = -1;
        while (price < 0) {
            System.out.print("Enter Price: ");
            try {
                if (!scanner.hasNextLine())
                    return;
                String input = scanner.nextLine().trim();
                if (input.isEmpty())
                    continue;
                price = Double.parseDouble(input);
                if (price < 0)
                    System.out.println("Price cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format.");
            }
        }

        String sql = "INSERT INTO menu_items (item_name, price) VALUES (?, ?)";
        executeUpdate(sql, name, price);
    }

    private static void viewItems() {
        String selectSQL = "SELECT * FROM menu_items";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(selectSQL);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n--- Menu Items ---");
            System.out.printf("%-15s %-30s %-10s%n", "Serial Number", "Item Name", "Price");
            System.out.println("----------------------------------------------------------");
            while (rs.next()) {
                int serial = rs.getInt("serial_number");
                String name = rs.getString("item_name");
                double price = rs.getDouble("price");
                System.out.printf("%-15d %-30s %-10.2f%n", serial, name, price);
            }
        } catch (SQLException e) {
            System.err.println("Error reading data: " + e.getMessage());
        }
    }

    private static void updateItem(Scanner scanner) {
        int serial = -1;
        while (serial == -1) {
            System.out.print("Enter Serial Number to update: ");
            try {
                if (!scanner.hasNextLine())
                    return;
                String input = scanner.nextLine().trim();
                if (input.isEmpty())
                    continue;
                serial = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Serial Number.");
            }
        }

        System.out.println("What do you want to update?");
        System.out.println("1. Item Name");
        System.out.println("2. Price");
        System.out.println("3. Cancel");
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
                System.out.print("Enter New Item Name: ");
                if (!scanner.hasNextLine())
                    return;
                String name = scanner.nextLine();
                executeUpdate("UPDATE menu_items SET item_name = ? WHERE serial_number = ?", name, serial);
                break;
            case 2:
                double price = -1;
                while (price < 0) {
                    System.out.print("Enter New Price: ");
                    try {
                        if (!scanner.hasNextLine())
                            return;
                        String input = scanner.nextLine().trim();
                        if (input.isEmpty())
                            continue;
                        price = Double.parseDouble(input);
                        if (price < 0)
                            System.out.println("Price cannot be negative.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                }
                executeUpdate("UPDATE menu_items SET price = ? WHERE serial_number = ?", price, serial);
                break;
            case 3:
                System.out.println("Update cancelled.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void deleteItem(Scanner scanner) {
        int serial = -1;
        while (serial == -1) {
            System.out.print("Enter Serial Number to delete: ");
            try {
                if (!scanner.hasNextLine())
                    return;
                String input = scanner.nextLine().trim();
                if (input.isEmpty())
                    continue;
                serial = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Serial Number.");
            }
        }

        executeUpdate("DELETE FROM menu_items WHERE serial_number = ?", serial);
    }
}
