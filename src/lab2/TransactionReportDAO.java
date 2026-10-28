
package lab2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionReportDAO {

    public boolean exportTransactionHistory(String filePath) {

        String sql = """
                SELECT id, account_id, reference_id,
                       transaction_type, amount, created_at
                FROM transactions
                ORDER BY created_at
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();
             BufferedWriter writer =
                     new BufferedWriter(new FileWriter(filePath))) {

            // Write the CSV column headings
            writer.write(
                    "Transaction ID,Account ID,Reference ID,Type,Amount,Created At");
            writer.newLine();

            // Write each transaction as a CSV row
            while (resultSet.next()) {
                writer.write(
                        resultSet.getInt("id") + ","
                                + resultSet.getInt("account_id") + ","
                                + resultSet.getString("reference_id") + ","
                                + resultSet.getString("transaction_type") + ","
                                + resultSet.getBigDecimal("amount") + ","
                                + resultSet.getTimestamp("created_at")
                );
                writer.newLine();
            }

            System.out.println("Transaction history exported successfully!");
            return true;

        } catch (SQLException | IOException e) {
            System.out.println(
                    "Could not export transaction history: "
                            + e.getMessage());
            return false;
        }
    }
}

