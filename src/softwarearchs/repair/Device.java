package softwarearchs.repair;

import softwarearchs.exceptions.IllegalWarranty;
import softwarearchs.user.Client;

import java.util.Date;

public class Device {
    private String serialNumber;

    private String deviceType;
    private String deviceBrand;
    private String deviceModel;

    private Date dateOfPurchase;
    private Date warrantyExpiration;
    private Date prevRepair;
    private Date repairWarrantyExpiration;

    private boolean warrantyAvailable;
    private boolean repairWarrantyAvailable;

    private Client client;

    public Device(String serialNumber, String deviceType,
                  String deviceBrand, String deviceModel, Client client){
        this.serialNumber = serialNumber.toUpperCase();
        this.deviceType = deviceType;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.client = client;
        this.warrantyAvailable = false;
    }

    public Device (String serialNumber){
        this.serialNumber = serialNumber.toUpperCase();
    }

    public Device(){}

    public Device (Device device){

        this.serialNumber = device.serialNumber;
        this.deviceType = device.deviceType;
        this.deviceBrand = device.deviceBrand;
        this.deviceModel = device.deviceModel;

        this.dateOfPurchase = device.dateOfPurchase;
        this.warrantyExpiration = device.warrantyExpiration;
        this.prevRepair = device.prevRepair;
        this.repairWarrantyExpiration = device.repairWarrantyExpiration;

        this.warrantyAvailable = device.warrantyAvailable;
        this.repairWarrantyAvailable = device.repairWarrantyAvailable;

        this.client = device.client;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public String getDeviceBrand() {
        return deviceBrand;
    }
    public String getDeviceModel() {
        return deviceModel;
    }
    public Date getDateOfPurchase() {
        return dateOfPurchase;
    }
    public Date getWarrantyExpiration() {
        return warrantyExpiration;
    }
    public Date getPrevRepair() {
        return prevRepair;
    }
    public Date getRepairWarrantyExpiration() {
        return repairWarrantyExpiration;
    }
    public Client getClient() {
        return client;
    }

    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }
    public void setWarrantyExpiration(Date warrantyExpiration) {
        this.warrantyExpiration = warrantyExpiration;
    }
    public void setPrevRepair(Date prevRepair) {
        this.prevRepair = prevRepair;
    }
    public void setRepairWarrantyExpiration(Date repairWarrantyExpiration) {
        this.repairWarrantyExpiration = repairWarrantyExpiration;
    }
    public void setWarrantyAvailable(Boolean warrantyAvailable) {
        this.warrantyAvailable = warrantyAvailable;
    }
    public void setRepairWarrantyAvailable(Boolean repairWarrantyAvailable) {
        this.repairWarrantyAvailable = repairWarrantyAvailable;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public boolean Eq(Device device){
        if(device == null)
            return false;

        return (device.serialNumber.equals(serialNumber) && device.deviceBrand.equals(deviceBrand) &&
        device.deviceModel.equals(deviceModel) && deviceType.equals(deviceType));
    }

    public boolean absEq(Device device) throws NullPointerException{
        return (device.dateOfPurchase.equals(dateOfPurchase) &&
                device.warrantyExpiration.equals(warrantyExpiration) &&
                device.prevRepair.equals(prevRepair) &&
                device.repairWarrantyExpiration.equals(repairWarrantyExpiration));
    }

    public boolean isWarrantyValid() throws IllegalWarranty{
        if((dateOfPurchase == null && warrantyExpiration == null) ||
                (prevRepair == null && repairWarrantyExpiration != null)){
            throw new IllegalWarranty("Warranty date cannot be set without event date");
        }
        return true;
    }
}


