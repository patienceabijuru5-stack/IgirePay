
package lab2;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Lab2IntegrationTest {

    public static void main(String[] args) {

        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        ProcessedRequestDAO requestDAO = new ProcessedRequestDAO();

        String uniquePart = String.valueOf(System.currentTimeMillis());

        String fullName = "Jean Claude";
        String email = "jean.claude" + uniquePart + "@example.com";
        String phoneNumber = "078"
                + uniquePart.substring(uniquePart.length() - 7);

        // 1. Create a customer
        System.out.println("===== CREATE CUSTOMER =====");

        boolean customerCreated =
                customerDAO.addCustomer(fullName, email, phoneNumber);

        System.out.println("Customer created: " + customerCreated);

        // 2. Retrieve the customer's ID
        int customerId = -1;

        String customerSql =
                "SELECT id FROM customers WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(customerSql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    customerId = resultSet.getInt("id");
                    System.out.println("Customer ID: " + customerId);
                }
            }

        } catch (Exception e) {
            System.out.println("Could not retrieve customer: "
                    + e.getMessage());
            return;
        }

        if (customerId == -1) {
            System.out.println("Customer not found. Test stopped.");
            return;
        }

        // 3. Update the customer
        System.out.println("\n===== UPDATE CUSTOMER =====");

        boolean customerUpdated = customerDAO.updateCustomer(
                customerId,
                "Jean Claude Updated",
                email,
                phoneNumber
        );

        System.out.println("Customer updated: " + customerUpdated);

        // 4. Create two accounts
        System.out.println("\n===== CREATE ACCOUNTS =====");

        accountDAO.createAccount(
                customerId, "SAVINGS", new BigDecimal("1000.00"));

        accountDAO.createAccount(
                customerId, "WALLET", new BigDecimal("500.00"));

        // 5. Retrieve the two account IDs
        int firstAccountId = -1;
        int secondAccountId = -1;

        String accountSql =
                "SELECT id FROM accounts WHERE customer_id = ? "
                        + "ORDER BY id DESC LIMIT 2";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(accountSql)) {

            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    firstAccountId = resultSet.getInt("id");
                }

                if (resultSet.next()) {
                    secondAccountId = resultSet.getInt("id");
                }
            }

        } catch (Exception e) {
            System.out.println("Could not retrieve accounts: "
                    + e.getMessage());
            return;
        }

        if (firstAccountId == -1 || secondAccountId == -1) {
            System.out.println("Could not find both accounts.");
            return;
        }

        System.out.println("First account ID: " + firstAccountId);
        System.out.println("Second account ID: " + secondAccountId);

        // 6. Test deposit
        System.out.println("\n===== DEPOSIT =====");

        String depositReference = "DEP-" + uniquePart;

        boolean depositSuccess = transactionDAO.deposit(
                firstAccountId,
                new BigDecimal("200.00"),
                depositReference
        );

        System.out.println("Deposit successful: " + depositSuccess);

        // 7. Test withdrawal
        System.out.println("\n===== WITHDRAWAL =====");

        boolean withdrawalSuccess = transactionDAO.withdraw(
                firstAccountId,
                new BigDecimal("50.00"),
                "WDR-" + uniquePart
        );

        System.out.println("Withdrawal successful: "
                + withdrawalSuccess);

        // 8. Test transfer
        System.out.println("\n===== TRANSFER =====");

        boolean transferSuccess = transactionDAO.transfer(
                firstAccountId,
                secondAccountId,
                new BigDecimal("100.00"),
                "TRF-" + uniquePart
        );

        System.out.println("Transfer successful: " + transferSuccess);

        // 9. Test duplicate deposit
        System.out.println("\n===== DUPLICATE REQUEST TEST =====");

        BigDecimal balanceBefore =
                accountDAO.getAccountBalance(firstAccountId);

        boolean duplicateDeposit = transactionDAO.deposit(
                firstAccountId,
                new BigDecimal("200.00"),
                depositReference
        );

        BigDecimal balanceAfter =
                accountDAO.getAccountBalance(firstAccountId);

        System.out.println("Duplicate deposit accepted: "
                + duplicateDeposit);
        System.out.println("Balance before: " + balanceBefore);
        System.out.println("Balance after: " + balanceAfter);

        if (!duplicateDeposit
                && balanceBefore != null
                && balanceBefore.equals(balanceAfter)) {
            System.out.println(
                    "PASS: Duplicate deposit did not change the balance.");
        } else {
            System.out.println(
                    "CHECK: Duplicate request test needs attention.");
        }

        // 10. Test processed request tracking
        System.out.println("\n===== PROCESSED REQUEST TEST =====");

        String requestReference = "REQ-" + uniquePart;

        boolean requestRecorded =
                requestDAO.recordRequest(requestReference);

        boolean requestFound =
                requestDAO.isRequestProcessed(requestReference);

        boolean duplicateRecorded =
                requestDAO.recordRequest(requestReference);

        System.out.println("First request recorded: " + requestRecorded);
        System.out.println("Request found: " + requestFound);
        System.out.println("Duplicate request recorded: "
                + duplicateRecorded);

        // 11. Display the accounts
        System.out.println("\n===== ALL ACCOUNTS =====");
        accountDAO.getAllAccounts();

        // 12. Display transaction history
        System.out.println("\n===== FIRST ACCOUNT TRANSACTIONS =====");
        transactionDAO.getTransactionHistory(firstAccountId);

        System.out.println("\n===== SECOND ACCOUNT TRANSACTIONS =====");
        transactionDAO.getTransactionHistory(secondAccountId);

        System.out.println("\n===== INTEGRATION TEST FINISHED =====");
    }
}
