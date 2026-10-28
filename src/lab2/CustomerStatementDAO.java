
package lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerStatementDAO {

    public void generateCustomerStatement(int customerId) {

        String sql = """
                SELECT
                    c.id AS customer_id,
                    c.full_name,
                    a.id AS account_id,
                    t.id AS transaction_id,
                    t.reference_id,
                    t.transaction_type,
                    t.amount,
                    t.created_at
                FROM customers c
                JOIN accounts a ON c.id = a.customer_id
                JOIN transactions t ON a.id = t.account_id
                WHERE c.id = ?
                ORDER BY t.created_at
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                System.out.println("\n===== CUSTOMER TRANSACTION STATEMENT =====");

                boolean found = false;

                while (resultSet.next()) {
                    if (!found) {
                        System.out.println(
                                "Customer: "
                                        + resultSet.getString("full_name"));
                        System.out.println(
                                "Customer ID: "
                                        + resultSet.getInt("customer_id"));
                        found = true;
                    }

                    System.out.println("-----------------------------------");
                    System.out.println(
                            "Account ID: "
                                    + resultSet.getInt("account_id"));
                    System.out.println(
                            "Transaction ID: "
                                    + resultSet.getInt("transaction_id"));
                    System.out.println(
                            "Reference: "
                                    + resultSet.getString("reference_id"));
                    System.out.println(
                            "Type: "
                                    + resultSet.getString("transaction_type"));
                    System.out.println(
                            "Amount: "
                                    + resultSet.getBigDecimal("amount"));
                    System.out.println(
                            "Date: "
                                    + resultSet.getTimestamp("created_at"));
                }

                if (!found) {
                    System.out.println(
                            "No transactions found for this customer.");
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Could not generate customer statement: "
                            + e.getMessage());
        }
    }
}

