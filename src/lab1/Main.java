
package lab1;

public class Main {

    public static void main(String[] args) {

        // 1. Create a wallet account
        WalletAccount wallet =
                new WalletAccount(1, "WALLET001", 10000);

        // 2. Create a savings account
        SavingsAccount savings =
                new SavingsAccount(2, "SAVINGS001", 20000, 5000);

        // 3. Create a customer
        Customer customer =
                new Customer(
                        1,
                        "Patience Abijuru",
                        "patienceabijuru5@gmail.com",
                        "0798637626"
                );

        // 4. Create a transaction
        Transaction transaction =
                new Transaction(1, "IGP-10001", 2000, "DEPOSIT");

        // 5. Display the objects
        System.out.println("===== IGIREPAY OBJECTS =====");
        System.out.println(wallet);
        System.out.println(savings);
        System.out.println(customer);
        System.out.println(transaction);

        // 6. Test wallet deposit
        System.out.println("\n===== WALLET TRANSACTION =====");

        wallet.processTransaction("DEPOSIT", 2000);

        System.out.println("Wallet balance: " + wallet.getBalance());

        // 7. Test savings withdrawal
        System.out.println("\n===== SAVINGS TRANSACTION =====");

        try {
            savings.processTransaction("WITHDRAWAL", 18000);
        } catch (IllegalArgumentException e) {
            System.out.println("Transaction rejected: " + e.getMessage());
        }

        System.out.println("Savings balance: " + savings.getBalance());

        // 8. Create PaymentManager
        System.out.println("\n===== PAYMENT MANAGER =====");

        PaymentManager manager = new PaymentManager();

        manager.addAccount(wallet);
        manager.addAccount(savings);
        manager.addTransaction(transaction);

        // 9. Display accounts and transaction history
        System.out.println("\n===== ACCOUNTS =====");
        manager.displayAccounts();

        System.out.println("\n===== TRANSACTION HISTORY =====");
        manager.displayTransactionHistory();

        // 10. Test duplicate reference detection
        System.out.println("\n===== DUPLICATE REFERENCE TEST =====");

        manager.processReference("IGP-20001");
        manager.processReference("IGP-20001");

        // 11. Test failed transaction logging
        System.out.println("\n===== FAILED TRANSACTIONS =====");

        manager.logFailedTransaction(
                "IGP-20002",
                "Insufficient balance."
        );

        manager.displayFailedTransactions();
    }
}



