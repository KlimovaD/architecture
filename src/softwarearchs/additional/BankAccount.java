package softwarearchs.additional;

import softwarearchs.enums.InvoiceStatus;
import softwarearchs.exceptions.InsufficientFunds;
import softwarearchs.exceptions.InvalidPaymentData;
import softwarearchs.repair.Invoice;
import softwarearchs.user.Client;

import java.util.Date;

public class BankAccount {
    private String accountNumber;
    private Client client;
    private Date validTill;
    private double balance;
    private int cvc;

    public BankAccount(String accountNumber, Client client, Date validTill, int cvc){
        this.accountNumber = accountNumber;
        this.client = client;
        this.validTill = validTill;
        this.cvc = cvc;
        this.balance = 0.0;
    }


    public String getAccountNumber() {
        return accountNumber;
    }
    public Client getClient() {
        return client;
    }
    public Date getValidTill() {
        return validTill;
    }
    public int getCvc() {
        return cvc;
    }
    public double getBalance() {return balance; }

    public void setValidTill(Date validTill) {
        this.validTill = validTill;
    }
    public void setCvc(int cvc) {
        this.cvc = cvc;
    }
    public void setClient(Client client) { this.client = client; }
    public void setBalance(Double balance) {this.balance = balance; }

    public boolean Eq(BankAccount account) throws InvalidPaymentData{
        if (account.getAccountNumber().equals(this.accountNumber) &&
                account.getClient().equals(this.client) &&
                account.validTill.equals(this.validTill) &&
                account.cvc == this.cvc)
            return true;
        throw new InvalidPaymentData("Invalid payment data");
    }

    public boolean payForRepair(Invoice invoice) throws InsufficientFunds, InvalidPaymentData{
        if(invoice.getStatus().equals(InvoiceStatus.Paid))
            throw new InvalidPaymentData("Attempt to pay for warranty repair");
        if(invoice.getPrice() > balance)
            throw new InsufficientFunds("Insufficient funds");

        balance -= invoice.getPrice();
        return true;
    }

    public boolean isValid() throws InvalidPaymentData{
        if(validTill.before(new Date()))
            throw new InvalidPaymentData("Bank account expired");

        return true;
    }

}
