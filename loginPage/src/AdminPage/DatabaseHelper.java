package AdminPage;

import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:products.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "category TEXT NOT NULL" +
                ");";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearAllData() {
        String sql = "DELETE FROM products";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("All data deleted from 'products' table.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
