package UserPage;

import javax.swing.*;
import java.sql.*;

import static AdminPage.DatabaseHelper.connect;

public class DBHelper {
    private static final String DB_URL = "jdbc:sqlite:purchases.db";
    private static boolean driverLoaded = false;

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            driverLoaded = true;
            System.out.println("✅ SQLite JDBC driver loaded successfully");

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                // NOTE: Uncomment the next line only during development if you want to recreate schema:
                // stmt.execute("DROP TABLE IF EXISTS purchases;");

                String sql = """
                CREATE TABLE IF NOT EXISTS purchases (
                    customer_name TEXT NOT NULL,
                    contact TEXT NOT NULL,
                    total_amount REAL NOT NULL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;

                stmt.execute(sql);
                System.out.println("✅ Database table initialized");
            }
        } catch (ClassNotFoundException e) {
            showErrorDialog("SQLite JDBC driver not found.\nPlease add sqlite-jdbc.jar to your project.");
            e.printStackTrace();
        } catch (SQLException e) {
            showErrorDialog("Database initialization failed:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            throw new SQLException("JDBC driver not initialized");
        }
        return DriverManager.getConnection(DB_URL);
    }

    // New 3-arg signature
    public static void savePurchase(String customerName, String contact, double totalAmount) {
        if (!driverLoaded) {
            showErrorDialog("Database driver not available");
            return;
        }

        String sql = """
        INSERT INTO purchases (customer_name, contact, total_amount)
        VALUES (?, ?, ?);
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerName);
            pstmt.setString(2, contact);
            pstmt.setDouble(3, totalAmount);

            pstmt.executeUpdate();
            System.out.println("✅ Purchase saved for customer: " + customerName);
        } catch (SQLException e) {
            showErrorDialog("Failed to save purchase:\n" + e.getMessage());
            e.printStackTrace();
        }


    }

    public static ResultSet getAllPurchases() {
        String query = "SELECT customer_id, customer_name, customer_contact, total_amount, timestamp FROM purchases";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
