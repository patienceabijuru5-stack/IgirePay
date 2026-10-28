
package lab3;

import java.util.Scanner;
import lab2.CustomerDAO;
import lab2.AccountDAO;
import lab2.TransactionReportDAO;
import lab2.DailyTransactionReportDAO;
import lab2.CustomerStatementDAO;
public class IgirePayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("==================================");
        System.out.println("     IGIREPAY PAYMENT SYSTEM");
        System.out.println("==================================");

        while (running) {

            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Register Customer");
            System.out.println("2. Update Customer Information");
            System.out.println("3. View Customer Accounts");
            System.out.println("4. Create Wallet Account");
            System.out.println("5. Create Savings Account");
            System.out.println("6. Check Account Balance");
            System.out.println("7. Deposit Money");
            System.out.println("8. Withdraw Money");
            System.out.println("9. Transfer Money");
            System.out.println("10. View Transaction History");
            System.out.println("11. Delete Inactive Account");
            System.out.println("12. Export Transaction History to CSV");
            System.out.println("13. Generate Daily Transaction Summary");
            System.out.println("14. Generate Customer Transaction Statement");
            System.out.println("15. Create Account PIN");
            System.out.println("16. Validate Account PIN");
            System.out.println("17. Change Account PIN");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    System.out.println("\n===== REGISTER CUSTOMER =====");

                    System.out.print("Enter full name: ");
                    String fullName = scanner.nextLine();

                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();

                    System.out.print("Enter phone number: ");
                    String phoneNumber = scanner.nextLine();

                    CustomerDAO customerDAO = new CustomerDAO();

                    boolean customerCreated = customerDAO.addCustomer(
                            fullName, email, phoneNumber
                    );

                    if (customerCreated) {
                        System.out.println("Customer registered successfully!");
                    } else {
                        System.out.println("Customer registration failed.");
                    }
                    break;
                case "2":
                    System.out.println("\n===== UPDATE CUSTOMER =====");

                    System.out.print("Enter customer ID: ");
                    int customerId = Integer.parseInt(scanner.nextLine());

                    System.out.print("Enter new full name: ");
                    String newFullName = scanner.nextLine();

                    System.out.print("Enter new email: ");
                    String newEmail = scanner.nextLine();

                    System.out.print("Enter new phone number: ");
                    String newPhoneNumber = scanner.nextLine();

                    customerDAO = new CustomerDAO();

                    boolean updated = customerDAO.updateCustomer(
                            customerId,
                            newFullName,
                            newEmail,
                            newPhoneNumber
                    );

                    if (updated) {
                        System.out.println("Customer updated successfully!");
                    } else {
                        System.out.println("Customer update failed. Check the customer ID.");
                    }

                    break;


                case "3":
                    System.out.println("\n===== VIEW CUSTOMER ACCOUNTS =====");

                    AccountDAO accountDAO = new AccountDAO();

                    accountDAO.getAllAccounts();

                    break;


                case "4": {
                    System.out.println("\n===== CREATE WALLET ACCOUNT =====");

                    try {
                        System.out.print("Enter customer ID: ");
                        int walletCustomerId = Integer.parseInt(scanner.nextLine());

                        System.out.print("Enter initial balance: ");
                        java.math.BigDecimal initialBalance =
                                new java.math.BigDecimal(scanner.nextLine());

                        lab2.AccountDAO walletDAO = new lab2.AccountDAO();

                        boolean created = walletDAO.createAccount(
                                walletCustomerId,
                                "WALLET",
                                initialBalance
                        );

                        if (created) {
                            System.out.println("Wallet account created successfully!");
                        } else {
                            System.out.println("Wallet account creation failed.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Enter a numeric customer ID and a valid balance."
                        );
                    }

                    break;
                }


                case "5": {
                    System.out.println("\n===== CREATE SAVINGS ACCOUNT =====");

                    try {
                        System.out.print("Enter customer ID: ");
                        int savingsCustomerId =
                                Integer.parseInt(scanner.nextLine());

                        System.out.print("Enter initial balance: ");
                        java.math.BigDecimal initialBalance =
                                new java.math.BigDecimal(scanner.nextLine());

                        lab2.AccountDAO savingsDAO = new lab2.AccountDAO();

                        boolean created = savingsDAO.createAccount(
                                savingsCustomerId,
                                "SAVINGS",
                                initialBalance
                        );

                        if (created) {
                            System.out.println("Savings account created successfully!");
                        } else {
                            System.out.println("Savings account creation failed.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Enter a numeric customer ID and a valid balance."
                        );
                    }

                    break;
                }

                case "6": {
                    System.out.println("\n===== CHECK ACCOUNT BALANCE =====");
                    System.out.print("Enter account ID: ");

                    String input = scanner.nextLine();

                    try {
                        int balanceAccountId = Integer.parseInt(input);

                        if (balanceAccountId <= 0) {
                            System.out.println("Account ID must be a positive number.");
                            break;
                        }

                        lab2.AccountDAO balanceDAO = new lab2.AccountDAO();

                        java.math.BigDecimal balance =
                                balanceDAO.getAccountBalance(balanceAccountId);

                        if (balance != null) {
                            System.out.println("Current account balance: " + balance);
                        } else {
                            System.out.println("Account not found.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a numeric account ID.");
                    }

                    break;
                }



                case "7": {
                    System.out.println("\n===== DEPOSIT MONEY =====");

                    try {
                        System.out.print("Enter account ID: ");
                        int depositAccountId =
                                Integer.parseInt(scanner.nextLine());

                        if (depositAccountId <= 0) {
                            System.out.println("Account ID must be a positive number.");
                            break;
                        }

                        System.out.print("Enter amount to deposit: ");
                        java.math.BigDecimal depositAmount =
                                new java.math.BigDecimal(scanner.nextLine());

                        if (depositAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                            System.out.println("Deposit amount must be greater than zero.");
                            break;
                        }

                        System.out.print("Enter a unique reference ID: ");
                        String depositReference = scanner.nextLine().trim();

                        if (depositReference.isEmpty()) {
                            System.out.println("Reference ID cannot be empty.");
                            break;
                        }

                        lab2.TransactionDAO transactionDAO =
                                new lab2.TransactionDAO();

                        boolean deposited = transactionDAO.deposit(
                                depositAccountId,
                                depositAmount,
                                depositReference
                        );

                        if (deposited) {
                            System.out.println("Deposit successful!");
                        } else {
                            System.out.println(
                                    "Deposit failed. Check the account, amount, or reference ID.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Enter a valid account ID and numeric amount.");
                    }

                    break;
                }


                case "8": {
                    System.out.println("\n===== WITHDRAW MONEY =====");

                    try {
                        System.out.print("Enter account ID: ");
                        int withdrawalAccountId =
                                Integer.parseInt(scanner.nextLine());

                        if (withdrawalAccountId <= 0) {
                            System.out.println("Account ID must be a positive number.");
                            break;
                        }

                        System.out.print("Enter amount to withdraw: ");
                        java.math.BigDecimal withdrawalAmount =
                                new java.math.BigDecimal(scanner.nextLine());

                        if (withdrawalAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                            System.out.println("Withdrawal amount must be greater than zero.");
                            break;
                        }

                        System.out.print("Enter a unique reference ID: ");
                        String withdrawalReference = scanner.nextLine().trim();

                        if (withdrawalReference.isEmpty()) {
                            System.out.println("Reference ID cannot be empty.");
                            break;
                        }

                        lab2.TransactionDAO transactionDAO =
                                new lab2.TransactionDAO();

                        boolean withdrawn = transactionDAO.withdraw(
                                withdrawalAccountId,
                                withdrawalAmount,
                                withdrawalReference
                        );

                        if (withdrawn) {
                            System.out.println("Withdrawal successful!");

                            System.out.print("Enter your six-digit PIN: ");
                            String withdrawalPIN = scanner.nextLine();

                            AccountPINDAO pinDAO = new AccountPINDAO();

                            if (!pinDAO.validatePIN(withdrawalAccountId, withdrawalPIN)) {
                                System.out.println("Invalid account ID or PIN.");
                                break;
                            }

                        } else {
                            System.out.println(
                                    "Withdrawal failed. Check the account, amount, or reference ID.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Enter a valid account ID and numeric amount.");
                    }

                    break;
                }


                case "9": {
                    System.out.println("\n===== TRANSFER MONEY =====");

                    try {
                        System.out.print("Enter source account ID: ");
                        int fromAccountId = Integer.parseInt(scanner.nextLine());

                        if (fromAccountId <= 0) {
                            System.out.println("Source account ID must be positive.");
                            break;
                        }

                        System.out.print("Enter destination account ID: ");
                        int toAccountId = Integer.parseInt(scanner.nextLine());

                        if (toAccountId <= 0) {
                            System.out.println("Destination account ID must be positive.");
                            break;
                        }

                        System.out.print("Enter your six-digit PIN: ");
                        String transferPIN = scanner.nextLine();

                        AccountPINDAO pinDAO = new AccountPINDAO();

                        if (!pinDAO.validatePIN(fromAccountId, transferPIN)) {
                            System.out.println("Invalid source account ID or PIN.");
                            break;
                        }


                        if (fromAccountId == toAccountId) {
                            System.out.println("Source and destination accounts must be different.");
                            break;
                        }

                        System.out.print("Enter amount to transfer: ");
                        java.math.BigDecimal transferAmount =
                                new java.math.BigDecimal(scanner.nextLine());

                        if (transferAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                            System.out.println("Transfer amount must be greater than zero.");
                            break;
                        }

                        System.out.print("Enter a unique reference ID: ");
                        String transferReference = scanner.nextLine().trim();

                        if (transferReference.isEmpty()) {
                            System.out.println("Reference ID cannot be empty.");
                            break;
                        }

                        lab2.TransactionDAO transactionDAO =
                                new lab2.TransactionDAO();

                        boolean transferred = transactionDAO.transfer(
                                fromAccountId,
                                toAccountId,
                                transferAmount,
                                transferReference
                        );

                        if (transferred) {
                            System.out.println("Transfer successful!");
                        } else {
                            System.out.println(
                                    "Transfer failed. Check the accounts, balance, amount, or reference ID.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Account IDs must be whole numbers, and the amount must be numeric.");
                    }

                    break;
                }


                case "10": {
                    System.out.println("\n===== TRANSACTION HISTORY =====");

                    try {
                        System.out.print("Enter account ID: ");
                        int historyAccountId =
                                Integer.parseInt(scanner.nextLine());

                        if (historyAccountId <= 0) {
                            System.out.println("Account ID must be a positive number.");
                            break;
                        }

                        lab2.TransactionDAO historyDAO =
                                new lab2.TransactionDAO();

                        historyDAO.getTransactionHistory(historyAccountId);

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Please enter a numeric account ID.");
                    }

                    break;
                }


                case "11": {
                    System.out.println("\n===== DELETE INACTIVE ACCOUNT =====");

                    try {
                        System.out.print("Enter account ID: ");
                        int inactiveAccountId =
                                Integer.parseInt(scanner.nextLine());

                        if (inactiveAccountId <= 0) {
                            System.out.println("Account ID must be a positive number.");
                            break;
                        }

                        lab2.AccountDAO inactiveAccountDAO =
                                new lab2.AccountDAO();

                        inactiveAccountDAO.deleteInactiveAccount(inactiveAccountId);

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Please enter a numeric account ID.");
                    }

                    break;
                }


                case "12": {
                    TransactionReportDAO reportDAO = new TransactionReportDAO();

                    String filePath = "transaction_history.csv";

                    reportDAO.exportTransactionHistory(filePath);
                    break;
                }
                case "13": {
                    java.util.Scanner dateScanner =
                            new java.util.Scanner(System.in);

                    System.out.print("Enter report date (YYYY-MM-DD): ");
                    String dateInput = dateScanner.nextLine();

                    try {
                        java.time.LocalDate reportDate =
                                java.time.LocalDate.parse(dateInput);

                        DailyTransactionReportDAO reportDAO =
                                new DailyTransactionReportDAO();

                        reportDAO.generateDailySummary(reportDate);

                    } catch (java.time.format.DateTimeParseException e) {
                        System.out.println(
                                "Invalid date. Please use YYYY-MM-DD.");
                    }

                    break;
                }

                case "14": {
                    System.out.print("Enter customer ID: ");
                    String customerIdInput = scanner.nextLine();

                    try {
                        int statementCustomerId =
                                Integer.parseInt(customerIdInput);

                        if (statementCustomerId <= 0) {
                            System.out.println("Customer ID must be positive.");
                        } else {
                            CustomerStatementDAO statementDAO =
                                    new CustomerStatementDAO();

                            statementDAO.generateCustomerStatement(
                                    statementCustomerId);
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Enter a numeric customer ID.");
                    }

                    break;
                }

                case "15": {
                    System.out.print("Enter account ID: ");
                    String accountInput = scanner.nextLine();

                    System.out.print("Create a six-digit PIN: ");
                    String pin = scanner.nextLine();

                    try {
                        int accountId = Integer.parseInt(accountInput);
                        AccountPINDAO pinDAO = new AccountPINDAO();

                        if (pinDAO.createPIN(accountId, pin)) {
                            System.out.println("PIN created successfully!");
                        } else {
                            System.out.println("PIN creation failed.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Account ID must be a number.");
                    }

                    break;
                }

                case "16": {
                    System.out.print("Enter account ID: ");
                    String accountInput = scanner.nextLine();

                    System.out.print("Enter your PIN: ");
                    String pin = scanner.nextLine();

                    try {
                        int accountId = Integer.parseInt(accountInput);
                        AccountPINDAO pinDAO = new AccountPINDAO();

                        if (pinDAO.validatePIN(accountId, pin)) {
                            System.out.println("PIN validated successfully!");
                        } else {
                            System.out.println("Invalid account ID or PIN.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Account ID must be a number.");
                    }

                    break;
                }

                case "17": {
                    System.out.print("Enter account ID: ");
                    String accountInput = scanner.nextLine();

                    System.out.print("Enter your current PIN: ");
                    String oldPIN = scanner.nextLine();

                    System.out.print("Enter your new six-digit PIN: ");
                    String newPIN = scanner.nextLine();

                    try {
                        int accountId = Integer.parseInt(accountInput);
                        AccountPINDAO pinDAO = new AccountPINDAO();

                        if (pinDAO.changePIN(accountId, oldPIN, newPIN)) {
                            System.out.println("PIN changed successfully!");
                        } else {
                            System.out.println("PIN change failed.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Account ID must be a number.");
                    }

                    break;
                }



                case "0":
                    running = false;
                    System.out.println("Thank you for using IgirePay!");
                    break;
                default:
                    System.out.println(
                            "Invalid choice. Please enter a number from 0 to 11.");
            }
        }

        scanner.close();
    }
}

