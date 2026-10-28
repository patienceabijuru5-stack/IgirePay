
package lab3;

import lab2.DatabaseConnection;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class AccountPINDAO {

    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private String hashPIN(String pin, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(
                pin.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return ITERATIONS + ":"
                    + Base64.getEncoder().encodeToString(salt) + ":"
                    + Base64.getEncoder().encodeToString(hash);
        } finally {
            spec.clearPassword();
        }
    }

    private String createHash(String pin) throws Exception {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return hashPIN(pin, salt);
    }

    private boolean isValidPINFormat(String pin) {
        return pin != null && pin.matches("\\d{6}");
    }

    public boolean createPIN(int accountId, String pin) {
        if (accountId <= 0 || !isValidPINFormat(pin)) {
            System.out.println(
                    "Enter a positive account ID and a six-digit PIN.");
            return false;
        }

        String sql = """
                INSERT INTO account_pins (account_id, pin_hash)
                VALUES (?, ?)
                """;

        try {
            String pinHash = createHash(pin);

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, accountId);
                statement.setString(2, pinHash);

                return statement.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Could not create PIN: " + e.getMessage());
            return false;
        }
    }

    public boolean validatePIN(int accountId, String pin) {
        if (accountId <= 0 || pin == null) {
            return false;
        }

        String sql = """
                SELECT pin_hash
                FROM account_pins
                WHERE account_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                }

                String[] parts = resultSet.getString("pin_hash").split(":");
                int iterations = Integer.parseInt(parts[0]);
                byte[] salt = Base64.getDecoder().decode(parts[1]);
                byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

                PBEKeySpec spec = new PBEKeySpec(
                        pin.toCharArray(), salt, iterations,
                        expectedHash.length * 8);

                try {
                    SecretKeyFactory factory =
                            SecretKeyFactory.getInstance(
                                    "PBKDF2WithHmacSHA256");

                    byte[] actualHash =
                            factory.generateSecret(spec).getEncoded();

                    return java.security.MessageDigest.isEqual(
                            expectedHash, actualHash);
                } finally {
                    spec.clearPassword();
                }
            }
        } catch (Exception e) {
            System.out.println("Could not validate PIN: " + e.getMessage());
            return false;
        }
    }

    public boolean changePIN(int accountId, String oldPIN, String newPIN) {
        if (!isValidPINFormat(newPIN)) {
            System.out.println("The new PIN must contain six digits.");
            return false;
        }

        if (!validatePIN(accountId, oldPIN)) {
            System.out.println("Incorrect current PIN.");
            return false;
        }

        String sql = """
                UPDATE account_pins
                SET pin_hash = ?, updated_at = CURRENT_TIMESTAMP
                WHERE account_id = ?
                """;

        try {
            String newHash = createHash(newPIN);

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, newHash);
                statement.setInt(2, accountId);

                return statement.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Could not change PIN: " + e.getMessage());
            return false;
        }
    }
}
