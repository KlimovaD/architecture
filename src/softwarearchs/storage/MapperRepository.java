package softwarearchs.storage;

import softwarearchs.exceptions.CreationFailed;
import softwarearchs.exceptions.InvalidSignIn;
import softwarearchs.exceptions.InvalidUser;
import softwarearchs.repair.Device;
import softwarearchs.enums.RepairType;
import softwarearchs.enums.Role;
import softwarearchs.additional.BankAccount;
import softwarearchs.repair.Invoice;
import softwarearchs.repair.Receipt;
import softwarearchs.storage.mapper.*;
import softwarearchs.user.Client;
import softwarearchs.user.Master;
import softwarearchs.user.Receiver;
import softwarearchs.user.User;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Date;

public class MapperRepository {
    private static UserMapper userMapper;
    private static DeviceMapper deviceMapper;
    private static ReceiptMapper receiptMapper;
    private static BankMapper bankMapper;
    private static InvoiceMapper invoiceMapper;

    public MapperRepository() {
        if (userMapper == null) userMapper = new UserMapper();
        if (deviceMapper == null) deviceMapper = new DeviceMapper();
        if (receiptMapper == null) receiptMapper = new ReceiptMapper();
        if (bankMapper == null) bankMapper = new BankMapper();
        if (invoiceMapper == null) invoiceMapper = new InvoiceMapper();
    }

    public boolean addUser(User user, String pwd) throws InvalidUser, CreationFailed{
        try{
            userMapper.addUser(user, pwd);
        } catch (SQLException e){
            System.out.println(e.toString());
            return false;
        }
        return true;
    }

    public User findUser(String login) throws InvalidUser{
        try {
            return userMapper.findUser(login);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public User findUser(String name, String surname, String patronymic) throws InvalidUser{
        try {
            return userMapper.findUser(name, surname, patronymic);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public boolean updateUser(User user) throws InvalidUser{
        try{
            return userMapper.updateUser(user);
        } catch(SQLException e){
            System.out.println(e.toString());
        }
        return false;
    }

    public boolean deleteUser(String login) throws InvalidUser{
        try{
            return userMapper.deleteUser(login);
        } catch(SQLException e){
            System.out.println(e.toString());
        }
        return false;
    }

    public AbstractMap<String, User> findAllUsers() throws InvalidUser{
        try {
            return userMapper.findAll();
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public boolean signIn(String login, String pwd) throws InvalidSignIn{
        try{
            return userMapper.signIn(login, pwd);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return false;
    }

    public boolean addReceipt(Receipt receipt) throws CreationFailed{
        try{
            return receiptMapper.addReceipt(receipt);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return false;
    }

    public boolean updateReceipt(Receipt receipt){
        try{
            return receiptMapper.updateReceipt(receipt);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return false;
    }

    public Receipt findReceipt(String receiptNumber){
        try{
            return receiptMapper.findReceipt(receiptNumber);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public AbstractMap<String, Receipt> findAllReceipts(){
        try {
            return receiptMapper.findAll();
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public AbstractMap<String, Receipt> findByUser (User user){
        try {
            return receiptMapper.findByUser(user);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public boolean addInvoice(Invoice invoice) throws CreationFailed{
        try{
            return invoiceMapper.addInvoice(invoice);
        } catch(SQLException e){
            System.out.println(e.toString());
        }
        return false;
    }

    public Invoice findInvoice(String invoiceNumber){
        try {
            return invoiceMapper.findInvoice(invoiceNumber);
        } catch(SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public AbstractMap<String, Invoice> findAllInvoices(){
        try {
            return invoiceMapper.findAllInvoices();
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public AbstractMap<String, Invoice> findInvoiceByUser(User user){
        try{
            return invoiceMapper.findByUser(user);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public boolean updateInvoice(Invoice invoice){
        try{
            return invoiceMapper.updateInvoice(invoice);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return false;
    }

    public boolean addDevice(Device device) throws CreationFailed{
        try{
            return deviceMapper.addDevice(device);
        } catch (SQLException e){
            System.out.println(e.toString());
        }
        return false;
    }

    public Device findDevice(String serialNumber){
        try{
            return deviceMapper.findDevice(serialNumber);
        } catch(SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public boolean updateDevice(Device device){
        try{
            return deviceMapper.updateDevice(device);
        } catch(SQLException e){
            System.out.println(e.toString());
        }

        return false;
    }

    public BankAccount findAccount(String accountNumber){
        try{
            return bankMapper.findAccount(accountNumber);
        } catch(SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public boolean updateAccount(BankAccount account){
        try {
            return bankMapper.updateAccount(account);
        } catch(SQLException e) {
            System.out.println(e.toString());
        }
        return false;
    }

}
