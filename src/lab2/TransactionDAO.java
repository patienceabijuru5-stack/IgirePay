
package lab2;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDAO {

    // Check and register a request before processing it.
    // Must be called inside the same transaction as the balance changes.
    private boolean claimRequest(
            Connection connection,
            String referenceId) throws SQLException {

        String sql = """
                INSERT INTO processed_requests (reference_id)
                VALUES (?)
                ON CONFLICT (reference_id) DO NOTHING
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, referenceId);

            // False means this reference was already processed.
            return statement.executeUpdate() == 1;
        }
    }

    private boolean isValid(
            BigDecimal amount,
            String referenceId) {

        return amount != null
                && amount.compareTo(BigDecimal.ZERO) > 0
                && referenceId != null
                && !referenceId.isBlank();
    }

    // DEPOSIT
    public boolean deposit(
            int accountId,
            BigDecimal amount,
            String referenceId) {

        if (!isValid(amount, referenceId)) {
            System.out.println("Invalid deposit details.");
            return false;
        }

        String updateSql =
                "UPDATE accounts SET balance = balance + ? WHERE id = ?";

        String insertSql = """
                INSERT INTO transactions
                    (account_id, reference_id, transaction_type, amount)
                VALUES (?, ?, 'DEPOSIT', ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                if (!claimRequest(connection, referenceId)) {
                    connection.rollback();
                    System.out.println("Duplicate request rejected.");
                    return false;
                }

                try (PreparedStatement update =
                             connection.prepareStatement(updateSql)) {
                    update.setBigDecimal(1, amount);
                    update.setInt(2, accountId);

                    if (update.executeUpdate() == 0) {
                        connection.rollback();
                        System.out.println("Account not found.");
                        return false;
                    }
                }

                try (PreparedStatement insert =
                             connection.prepareStatement(insertSql)) {
                    insert.setInt(1, accountId);
                    insert.setString(2, referenceId);
                    insert.setBigDecimal(3, amount);
                    insert.executeUpdate();
                }

                connection.commit();
                System.out.println("Deposit completed successfully.");
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Deposit failed: " + e.getMessage());
                return false;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // WITHDRAWAL
    public boolean withdraw(
            int accountId,
            BigDecimal amount,
            String referenceId) {

        if (!isValid(amount, referenceId)) {
            System.out.println("Invalid withdrawal details.");
            return false;
        }

        String updateSql = """
                UPDATE accounts
                SET balance = balance - ?
                WHERE id = ? AND balance >= ?
                """;

        String insertSql = """
                INSERT INTO transactions
                    (account_id, reference_id, transaction_type, amount)
                VALUES (?, ?, 'WITHDRAWAL', ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                if (!claimRequest(connection, referenceId)) {
                    connection.rollback();
                    System.out.println("Duplicate request rejected.");
                    return false;
                }

                try (PreparedStatement update =
                             connection.prepareStatement(updateSql)) {
                    update.setBigDecimal(1, amount);
                    update.setInt(2, accountId);
                    update.setBigDecimal(3, amount);

                    if (update.executeUpdate() == 0) {
                        connection.rollback();
                        System.out.println(
                                "Account not found or insufficient balance.");
                        return false;
                    }
                }

                try (PreparedStatement insert =
                             connection.prepareStatement(insertSql)) {
                    insert.setInt(1, accountId);
                    insert.setString(2, referenceId);
                    insert.setBigDecimal(3, amount);
                    insert.executeUpdate();
                }

                connection.commit();
                System.out.println("Withdrawal completed successfully.");
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Withdrawal failed: " + e.getMessage());
                return false;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // TRANSFER
    public boolean transfer(
            int fromAccountId,
            int toAccountId,
            BigDecimal amount,
            String referenceId) {

        if (!isValid(amount, referenceId)
                || fromAccountId == toAccountId) {
            System.out.println("Invalid transfer details.");
            return false;
        }

        String debitSql = """
                UPDATE accounts
                SET balance = balance - ?
                WHERE id = ? AND balance >= ?
                """;

        String creditSql = """
                UPDATE accounts
                SET balance = balance + ?
                WHERE id = ?
                """;

        String insertSql = """
                INSERT INTO transactions
                    (account_id, reference_id, transaction_type, amount)
                VALUES (?, ?, 'TRANSFER', ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                if (!claimRequest(connection, referenceId)) {
                    connection.rollback();
                    System.out.println("Duplicate request rejected.");
                    return false;
                }

                // Deduct money from the sending account.
                try (PreparedStatement debit =
                             connection.prepareStatement(debitSql)) {
                    debit.setBigDecimal(1, amount);
                    debit.setInt(2, fromAccountId);
                    debit.setBigDecimal(3, amount);

                    if (debit.executeUpdate() == 0) {
                        connection.rollback();
                        System.out.println(
                                "Source account not found or insufficient balance.");
                        return false;
                    }
                }

                // Add money to the receiving account.
                try (PreparedStatement credit =
                             connection.prepareStatement(creditSql)) {
                    credit.setBigDecimal(1, amount);
                    credit.setInt(2, toAccountId);

                    if (credit.executeUpdate() == 0) {
                        connection.rollback();
                        System.out.println("Destination account not found.");
                        return false;
                    }
                }

                // Record the outgoing and incoming transfer.
                try (PreparedStatement insert =
                             connection.prepareStatement(insertSql)) {

                    insert.setInt(1, fromAccountId);
                    insert.setString(2, referenceId + "-OUT");
                    insert.setBigDecimal(3, amount);
                    insert.executeUpdate();

                    insert.setInt(1, toAccountId);
                    insert.setString(2, referenceId + "-IN");
                    insert.setBigDecimal(3, amount);
                    insert.executeUpdate();
                }

                connection.commit();
                System.out.println("Transfer completed successfully.");
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Transfer failed: " + e.getMessage());
                return false;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // READ TRANSACTION HISTORY
    public void getTransactionHistory(int accountId) {

        String sql = """
                SELECT id, reference_id, transaction_type, amount, created_at
                FROM transactions
                WHERE account_id = ?
                ORDER BY created_at DESC, id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                boolean found = false;

                while (resultSet.next()) {
                    found = true;

                    System.out.println(
                            "Transaction ID: " + resultSet.getInt("id"));
                    System.out.println(
                            "Reference: " + resultSet.getString("reference_id"));
                    System.out.println(
                            "Type: " + resultSet.getString("transaction_type"));
                    System.out.println(
                            "Amount: " + resultSet.getBigDecimal("amount"));
                    System.out.println(
                            "Date: " + resultSet.getTimestamp("created_at"));
                    System.out.println("----------------------");
                }

                if (!found) {
                    System.out.println("No transactions found.");
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Could not retrieve transaction history: "
                            + e.getMessage());
        }
    }
}
