
package lab2;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DailyTransactionReportDAO {

    public void generateDailySummary(LocalDate date) {

        String sql = """
                SELECT
                    COUNT(*) AS total_transactions,
                    COALESCE(
                        SUM(CASE
                            WHEN transaction_type = 'DEPOSIT'
                            THEN amount ELSE 0
                        END), 0) AS total_deposits,
                    COALESCE(
                        SUM(CASE
                            WHEN transaction_type = 'WITHDRAWAL'
                            THEN amount ELSE 0
                        END), 0) AS total_withdrawals,
                    COALESCE(
                        SUM(CASE
                            WHEN transaction_type = 'TRANSFER'
                            THEN amount ELSE 0
                        END), 0) AS total_transfers
                FROM transactions
                WHERE created_at >= ?
                  AND created_at < ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setTimestamp(
                    1, java.sql.Timestamp.valueOf(date.atStartOfDay()));

            statement.setTimestamp(
                    2, java.sql.Timestamp.valueOf(
                            date.plusDays(1).atStartOfDay()));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int totalTransactions =
                            resultSet.getInt("total_transactions");

                    BigDecimal deposits =
                            resultSet.getBigDecimal("total_deposits");

                    BigDecimal withdrawals =
                            resultSet.getBigDecimal("total_withdrawals");

                    BigDecimal transfers =
                            resultSet.getBigDecimal("total_transfers");

                    System.out.println("\n===== DAILY TRANSACTION SUMMARY =====");
                    System.out.println("Date: " + date);
                    System.out.println(
                            "Total transactions: " + totalTransactions);
                    System.out.println("Total deposits: " + deposits);
                    System.out.println("Total withdrawals: " + withdrawals);
                    System.out.println("Total transfers: " + transfers);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Could not generate daily summary: " + e.getMessage());
        }
    }
}

