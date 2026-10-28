package lab1;

import java.time.LocalDateTime;

public class Account {

    private int id;
    private String accountNumber;
    private double balance;
    private LocalDateTime createdAt;

    public Account(int id, String accountNumber, double balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero."
            );
        }

        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero."
            );
        }

        if (amount > balance) {
            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        balance -= amount;
    }


    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                '}';
    }

    // Put processTransaction() here, outside toString()
    public void processTransaction(String type, double amount) {
        if ("DEPOSIT".equalsIgnoreCase(type)) {
            deposit(amount);

        } else if ("WITHDRAWAL".equalsIgnoreCase(type)) {
            withdraw(amount);

        } else {
            throw new IllegalArgumentException(
                    "Invalid transaction type."
            );
        }
    }


}


