
package lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcessedRequestDAO {

    // CREATE: Record a processed request
    // Returns false if the reference already exists.
    public boolean recordRequest(String referenceId) {

        if (referenceId == null || referenceId.isBlank()) {
            System.out.println("Reference ID cannot be empty.");
            return false;
        }

        String sql = """
                INSERT INTO processed_requests (reference_id)
                VALUES (?)
                ON CONFLICT (reference_id) DO NOTHING
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, referenceId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Could not record request: "
                    + e.getMessage());
            return false;
        }
    }

    // READ: Check whether a request has already been processed
    public boolean isRequestProcessed(String referenceId) {

        String sql = """
                SELECT 1
                FROM processed_requests
                WHERE reference_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, referenceId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Could not check request: "
                    + e.getMessage());
            return false;
        }
    }
}

