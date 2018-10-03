package softwarearchs.repair;

import softwarearchs.enums.ReceiptStatus;
import softwarearchs.enums.RepairType;
import softwarearchs.exceptions.IllegalWarranty;
import softwarearchs.user.Client;
import softwarearchs.user.Master;
import softwarearchs.user.Receiver;

import java.util.Date;

public class Receipt {
    private String receiptNumber;
    private Date receiptDate;
    private RepairType repairType;
    private Device device;
    private Client client;
    private String malfuncDescr;
    private String note;
    private Master master;
    private ReceiptStatus status;
    private Receiver receiver;

    public Receipt (){
        this.receiptDate = new Date();
    }

    public Receipt(String receiptNumber, Date receiptDate, RepairType repairType, Device device, Client client, Receiver receiver, String malfuncDescr){
        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
        this.repairType = repairType;
        this.device = device;
        this.client = client;
        this.malfuncDescr = malfuncDescr;
        this.receiver = receiver;
    }

    public String getReceiptNumber() { return receiptNumber; }
    public Date getReceiptDate() {
        return receiptDate;
    }
    public RepairType getRepairType() {
        return repairType;
    }
    public Device getDevice() {
        return device;
    }
    public Client getClient() {
        return client;
    }
    public String getMalfuncDescr() {
        return malfuncDescr;
    }
    public String getNote() {
        return note;
    }
    public Master getMaster() {
        return master;
    }
    public ReceiptStatus getStatus() {
        return status;
    }
    public Receiver getReceiver() {
        return receiver;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public void setMalfuncDescr(String malfuncDescr) {
        this.malfuncDescr = malfuncDescr;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setMaster(Master master) {
        this.master = master;
    }
    public void setStatus(ReceiptStatus status) {
        this.status = status;
    }
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public boolean isClosed(){
        return status.equals(ReceiptStatus.Closed);
    }

    public boolean isWarrantyValid() throws IllegalWarranty {
        if(repairType.equals(RepairType.Warranty)) {
            if((device.getWarrantyExpiration() == null && device.getDateOfPurchase() == null) &&
                    (device.getRepairWarrantyExpiration() == null && device.getPrevRepair() == null)) {
                throw new IllegalWarranty("Couldn't set warranty repair without " +
                        "warranty expiration date");
            }
            if(device.getWarrantyExpiration() != null &&
                    device.getWarrantyExpiration().before(new Date()) ||
                    device.getRepairWarrantyExpiration() != null &&
                            device.getRepairWarrantyExpiration().before(new Date())) {
                throw new IllegalWarranty("Couldn't set warranty repair without " +
                        "warranty expiration date");
            }
        }
        return true;
    }
}
