package softwarearchs.user;

import softwarearchs.Main;
import softwarearchs.enums.Role;
import softwarearchs.exceptions.*;
import softwarearchs.repair.Device;
import softwarearchs.enums.ReceiptStatus;
import softwarearchs.enums.RepairType;
import softwarearchs.repair.Invoice;
import softwarearchs.repair.Receipt;
import softwarearchs.storage.MapperRepository;

import java.sql.SQLException;
import java.util.Date;

public class Receiver extends User{

    public Receiver(int id, String name, String surname, String patronymic, String login){
        super(id, name, surname, patronymic, login);
    }

    public Receiver(String name, String surname, String patronymic, String login){
        super(name, surname, patronymic, login);
    }

    public Receipt createReceipt(String receiptNumber, String receiptDate, String repairType,
                                 Device device, String deviceMalfunction, String deviceNote,
                                 String receiptStatus){
        Date date = Main.dateFromString(receiptDate);

        Receipt receipt = new Receipt(receiptNumber, date, RepairType.valueOf(repairType),
                device, device.getClient(), this, deviceMalfunction);
        receipt.setStatus(ReceiptStatus.valueOf(receiptStatus));
        receipt.setNote(deviceNote);

        return receipt;
    }

    public Device createDevice(String deviceSerial, String deviceType,
                                String deviceBrand, String deviceModel,
                                Client client, String devicePurchaseDate,
                                String deviceWarrantyExpiration, String devicePreviousRepair,
                                String deviceRepairWarrantyExpiration){


        Device device = new Device(deviceSerial.toUpperCase(), deviceType,
                deviceBrand, deviceModel, client);
        device.setDateOfPurchase(Main.dateFromString(devicePurchaseDate));
        device.setWarrantyExpiration(Main.dateFromString(deviceWarrantyExpiration));
        device.setPrevRepair(Main.dateFromString(devicePreviousRepair));
        device.setRepairWarrantyExpiration(Main.dateFromString(deviceRepairWarrantyExpiration));

        return device;
    }

    public Device addDevice(Device device) throws CreationFailed, IllegalWarranty {
        device.isWarrantyValid();
        (new MapperRepository()).addDevice(device);

        return device;
    }

    public Receipt setRecStatus(String status, Receipt receipt) throws AcessPermision {
        if(!status.equals(ReceiptStatus.Opened.toString()) &&
                !status.equals(ReceiptStatus.Waiting_for_Diagnosis.toString()) &&
                !status.equals(ReceiptStatus.Closed.toString()))
            throw new AcessPermision("Receiver can not set such status");

        receipt.setStatus(ReceiptStatus.valueOf(status));
        return receipt;
    }

    public User addUser(Role userRole, String name, String surname,
                        String patronymic, String login,
                        String phone, String email) throws CreationFailed, InvalidUser{
        try {
            (new MapperRepository()).findUser(login);
        } catch (InvalidUser e) {
            User user;
            switch (userRole) {
                case Receiver:
                    user = new Receiver(name, surname, patronymic, login);
                    break;
                case Master:
                    user = new Master(name, surname, patronymic, login);
                    break;
                case Client:
                    user = new Client(name, surname, patronymic, login);
                    break;
                default:
                    throw new InvalidUser("Role does not specified");
            }
            user.setPhoneNumber(phone);
            user.seteMail(email);
            return user;
        }
        throw new CreationFailed("User already exists");
    }

    public Invoice createInvoice(String currentDate, Receipt receipt, String price)
            throws CreationFailed{

        double priceValue = Double.parseDouble(price);
        if(priceValue == 0 && !RepairType.Warranty.equals(receipt.getRepairType()))
            throw new CreationFailed("Repair can not be free if it is not warranty");
        else if(RepairType.Warranty.equals(receipt.getRepairType()) && priceValue != 0)
            throw new CreationFailed("Repair can not be with price if it is warranty");

        Invoice invoice = new Invoice(Main.dateFromString(currentDate), receipt);
        invoice.setPrice(priceValue);
        (new MapperRepository()).addInvoice(invoice);
        return invoice;
    }
}
