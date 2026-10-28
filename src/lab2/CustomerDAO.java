
package lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    // CREATE: Add a new customer
    public boolean addCustomer(
            String fullName,
            String email,
            String phoneNumber) {

        String sql = """
                INSERT INTO customers (full_name, email, phone_number)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, phoneNumber);

            int rowsInserted = statement.executeUpdate();

            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Could not add customer: " + e.getMessage());
            return false;
        }
    }

    // READ: Display all customers
    public void getAllCustomers() {

        String sql = """
                SELECT id, full_name, email, phone_number
                FROM customers
                ORDER BY id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            boolean found = false;

            while (resultSet.next()) {
                found = true;

                int id = resultSet.getInt("id");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone_number");

                System.out.println("Customer ID: " + id);
                System.out.println("Full Name: " + fullName);
                System.out.println("Email: " + email);
                System.out.println("Phone: " + phoneNumber);
                System.out.println("----------------------");
            }

            if (!found) {
                System.out.println("No customers found.");
            }

        } catch (SQLException e) {
            System.out.println("Could not retrieve customers: " + e.getMessage());
        }
    }

    // UPDATE: Change customer details
    public boolean updateCustomer(
            int id,
            String fullName,
            String email,
            String phoneNumber) {

        String sql = """
                UPDATE customers
                SET full_name = ?, email = ?, phone_number = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, phoneNumber);
            statement.setInt(4, id);

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Could not update customer: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Delete a customer by ID
    public boolean deleteCustomer(int id) {

        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Could not delete customer: " + e.getMessage());
            return false;
        }
    }
}


