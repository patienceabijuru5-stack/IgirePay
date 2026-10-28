
package lab2;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    // 1. CREATE: Create a new account
    public boolean createAccount(
            int customerId,
            String accountType,
            BigDecimal initialBalance) {

        if (customerId <= 0) {
            System.out.println("Customer ID must be a positive number.");
            return false;
        }

        if (accountType == null
                || (!accountType.equals("WALLET")
                && !accountType.equals("SAVINGS"))) {
            System.out.println("Account type must be WALLET or SAVINGS.");
            return false;
        }

        if (initialBalance == null
                || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Initial balance cannot be negative.");
            return false;
        }
        String sql = """
                INSERT INTO accounts
                    (customer_id, account_type, balance)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);
            statement.setString(2, accountType);
            statement.setBigDecimal(3, initialBalance);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Could not create account: "
                    + e.getMessage());
            return false;
        }
    }

    // 2. READ: Display all accounts
    public void getAllAccounts() {

        String sql = "SELECT * FROM accounts ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("Account ID: "
                        + resultSet.getInt("id"));
                System.out.println("Customer ID: "
                        + resultSet.getInt("customer_id"));
                System.out.println("Account Type: "
                        + resultSet.getString("account_type"));
                System.out.println("Balance: "
                        + resultSet.getBigDecimal("balance"));
                System.out.println("----------------------");
            }

        } catch (SQLException e) {
            System.out.println("Could not retrieve accounts: "
                    + e.getMessage());
        }
    }

    public java.math.BigDecimal getAccountBalance(int accountId) {
        String sql = "SELECT balance FROM accounts WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("balance");
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not get account balance: " + e.getMessage());
        }

        return null;
    }


    // 3. READ: Find an account by ID
    public void getAccountById(int accountId) {

        String sql = "SELECT * FROM accounts WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Account ID: "
                            + resultSet.getInt("id"));
                    System.out.println("Customer ID: "
                            + resultSet.getInt("customer_id"));
                    System.out.println("Account Type: "
                            + resultSet.getString("account_type"));
                    System.out.println("Balance: "
                            + resultSet.getBigDecimal("balance"));
                } else {
                    System.out.println("Account not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not find account: "
                    + e.getMessage());
        }
    }

    // 4. UPDATE: Set an account balance

    public boolean updateBalance(
            int accountId,
            BigDecimal newBalance) {

        if (newBalance == null
                || newBalance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Balance cannot be negative.");
            return false;
        }

        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, newBalance);
            statement.setInt(2, accountId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Could not update balance: "
                    + e.getMessage());
            return false;
        }
    }

    public boolean deleteInactiveAccount(int accountId) {
        String sql = "DELETE FROM accounts "
                + "WHERE id = ? "
                + "AND balance = 0 "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM transactions "
                + "WHERE account_id = accounts.id)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Inactive account deleted successfully!");
                return true;
            } else {
                System.out.println(
                        "Account not found, has a balance, or has transaction records.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error deleting account: " + e.getMessage());
            return false;
        }
    }
}


