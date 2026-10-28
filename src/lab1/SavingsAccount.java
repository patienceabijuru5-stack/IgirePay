
package lab1;

public class SavingsAccount extends Account {

    private double minimumBalance;

    public SavingsAccount(int id, String accountNumber,
                          double balance, double minimumBalance) {
        super(id, accountNumber, balance);
        this.minimumBalance = minimumBalance;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        if (minimumBalance < 0) {
            throw new IllegalArgumentException(
                    "Minimum balance cannot be negative."
            );
        }
        this.minimumBalance = minimumBalance;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        if (getBalance() - amount < minimumBalance) {
            throw new IllegalArgumentException(
                    "Withdrawal would go below minimum balance."
            );
        }

        super.withdraw(amount);
    }

    @Override
    public String toString() {
        return "SavingsAccount{" + super.toString() +
                ", minimumBalance=" + minimumBalance + "}";
    }

    @Override
    public void processTransaction(String type, double amount) {
        if ("WITHDRAWAL".equalsIgnoreCase(type)) {
            withdraw(amount);
            System.out.println(
                    "Savings withdrawal processed successfully."
            );
        } else {
            super.processTransaction(type, amount);
        }
    }

}

