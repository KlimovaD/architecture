package softwarearchs.repair;

import softwarearchs.enums.InvoiceStatus;
import softwarearchs.enums.RepairType;
import softwarearchs.repair.Receipt;
import softwarearchs.user.Client;
import softwarearchs.user.Receiver;

import java.util.Date;

public class Invoice {
    private String invoiceNumber;
    private Date invoiceDate;
    private Receipt receipt;
    private double price;
    private Client client;
    private Receiver receiver;
    private InvoiceStatus status;

    public Invoice(Date invoiceDate, Receipt receipt){
        this.invoiceNumber = receipt.getReceiptNumber();
        this.invoiceDate = invoiceDate;
        this.receipt = receipt;
        this.client = receipt.getClient();
        this.receiver = receipt.getReceiver();
        this.status = receipt.getRepairType()
                .equals(RepairType.Warranty) ? InvoiceStatus.Paid : InvoiceStatus.Waiting_For_Payment;
    }

    public Receipt getReceipt() { return  this.receipt; }
    public double getPrice() { return this.price; }
    public InvoiceStatus getStatus() { return this.status; }
    public String getInvoiceNumber() { return this.invoiceNumber; }
    public Date getInvoiceDate() { return this.invoiceDate; }
    public Client getClient() { return this.client; }
    public Receiver getReceiver() { return this.receiver; }

    public void setReceipt(Receipt receipt) { this.receipt = receipt; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public void setClient(Client client) { this.client = client; }
    public void setReceiver(Receiver receiver) {this.receiver = receiver; }
}
