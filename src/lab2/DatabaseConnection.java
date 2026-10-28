
package lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/igirepay_db";

    public static Connection getConnection() throws SQLException {
        String user = System.getenv("IGIREPAY_DB_USER");
        String password = System.getenv("IGIREPAY_DB_PASSWORD");

        if (user == null || password == null) {
            throw new SQLException(
                    "Database environment variables are not configured.");
        }

        return DriverManager.getConnection(URL, user, password);
    }
}
