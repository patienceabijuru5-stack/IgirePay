
package lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaymentManager {

    private List<Account> accounts = new ArrayList<>();

    private List<Transaction> transactionHistory =
            new ArrayList<>();

    private Set<String> processedReferences =
            new HashSet<>();

    private Map<String, String> failedTransactionLogs =
            new HashMap<>();

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    public boolean isReferenceProcessed(String referenceId) {
        return processedReferences.contains(referenceId);
    }

    public boolean processReference(String referenceId) {
        if (processedReferences.contains(referenceId)) {
            System.out.println("Duplicate transaction rejected.");
            return false;
        }

        processedReferences.add(referenceId);
        System.out.println("Reference accepted: " + referenceId);
        return true;
    }

    public void logFailedTransaction(
            String referenceId, String reason) {
        failedTransactionLogs.put(referenceId, reason);
    }

    public void displayAccounts() {
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    public void displayTransactionHistory() {
        for (Transaction transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }

    public void displayFailedTransactions() {
        for (Map.Entry<String, String> entry
                : failedTransactionLogs.entrySet()) {
            System.out.println(
                    "Reference: " + entry.getKey()
                            + ", Reason: " + entry.getValue()
            );
        }
    }
}

