
package lab1;

public class WalletAccount extends Account {

    public WalletAccount(int id, String accountNumber, double balance) {
        super(id, accountNumber, balance);
    }

    @Override
    public void deposit(double amount) {
        super.deposit(amount);
    }

    @Override
    public void withdraw(double amount) {
        super.withdraw(amount);
    }

    @Override
    public String toString() {
        return "WalletAccount{" + super.toString() + "}";
    }

    @Override
    public void processTransaction(String type, double amount) {
        if ("TRANSFER".equalsIgnoreCase(type)) {
            withdraw(amount);
            System.out.println(
                    "Wallet transfer processed successfully."
            );
        } else {
            super.processTransaction(type, amount);
        }
    }

}

