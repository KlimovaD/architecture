package softwarearchs.storage.mapper;

import softwarearchs.enums.ReceiptStatus;
import softwarearchs.enums.RepairType;
import softwarearchs.exceptions.CreationFailed;
import softwarearchs.repair.Receipt;
import softwarearchs.storage.Gateway;
import softwarearchs.user.Client;
import softwarearchs.user.Master;
import softwarearchs.user.Receiver;
import softwarearchs.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReceiptMapper {
    private static AbstractMap<String, Receipt> receipts = new HashMap<>();

    public boolean addReceipt(Receipt receipt) throws SQLException, CreationFailed{
        if(findReceipt(receipt.getReceiptNumber()) != null)
            throw new CreationFailed("Receipt already exists");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String statement = "INSERT INTO receipt VALUES (\"" + receipt.getReceiptNumber()
                + "\", DATE \'" + dateFormat.format(receipt.getReceiptDate())
                + "\', \"" + receipt.getDevice().getSerialNumber() + "\", " + receipt.getClient().getId()
                + ", " + receipt.getReceiver().getId() + ", \"" + receipt.getMalfuncDescr()
                + "\", " + (receipt.getNote() == null ? "NULL" : "\"" + receipt.getNote() + "\"")
                + ", " + (receipt.getMaster() == null ? "NULL" : "\"" + receipt.getMaster().getId() + "\"")
                + ", \"" + receipt.getRepairType() + "\", \"" + receipt.getStatus() + "\");";
        PreparedStatement insert = Gateway.getGateway().getConnection().prepareStatement(statement);
        insert.execute();
        receipts.put(receipt.getReceiptNumber(), receipt);
        return true;
    }

    public boolean updateReceipt(Receipt receipt) throws SQLException{
        String statement = "UPDATE receipt SET RepairType =\"" + receipt.getRepairType()
                + "\", Malfunction = \"" + receipt.getMalfuncDescr()
                + "\", Note = " + (receipt.getNote() == null ? "NULL" : "\"" + receipt.getNote() + "\"")
                + ", Status = \"" + receipt.getStatus()
                + "\", Master = " + (receipt.getMaster() == null ? "NULL" : "\"" + receipt.getMaster().getId() + "\"")
                + " WHERE id = \"" + receipt.getReceiptNumber() + "\";";
        PreparedStatement update = Gateway.getGateway().getConnection().prepareStatement(statement);
        if(update.executeUpdate() == 0)
            return false;

        receipts.replace(receipt.getReceiptNumber(), receipt);
        return true;
    }

    private static Receipt getReceipt(ResultSet rs) throws SQLException{
        Receipt receipt = new Receipt(rs.getString("id"), rs.getDate("ReceiptDate"),
                RepairType.valueOf(rs.getString("RepairType")),
                DeviceMapper.findDevice(rs.getString("Device")),
                (Client)UserMapper.findUser(rs.getInt("Client")),
                (Receiver)UserMapper.findUser(rs.getInt("Receiver")),
                rs.getString("Malfunction"));
        receipt.setNote(rs.getString("Note"));
        receipt.setMaster((Master)UserMapper.findUser(rs.getInt("Master")));
        receipt.setStatus(ReceiptStatus.valueOf(rs.getString("Status")));
        return receipt;
    }

    public static Receipt findReceipt(String receiptNumber) throws SQLException {
        if(receipts.containsKey(receiptNumber))
            return receipts.get(receiptNumber);

        String statement = "SELECT * FROM receipt WHERE id = \"" + receiptNumber + "\";";
        PreparedStatement find = Gateway.getGateway().getConnection().prepareStatement(statement);
        ResultSet rs = find.executeQuery();
        if(!rs.next()) return null;

        Receipt receipt = getReceipt(rs);
        receipts.put(receiptNumber, receipt);
        return receipt;
    }

    public AbstractMap<String, Receipt> findByUser(User user) throws SQLException {
        AbstractMap<String, Receipt> receiptsByClient = new HashMap<>();

        String userClass = user.getClass().getSimpleName();

        String query = "SELECT * FROM receipt WHERE " + userClass + " = " + user.getId()
                + " OR " + userClass + " is NULL;";
        Statement statement = Gateway.getGateway().getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);

        while(rs.next())
            receiptsByClient.put(rs.getString("id"), getReceipt(rs));

        return receiptsByClient;
    }

    public AbstractMap<String, Receipt> findAll() throws SQLException{
        receipts.clear();

        String query = "SELECT * FROM receipt;";
        Statement statement = Gateway.getGateway().getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);

        if(!rs.next()) {
            System.out.println("null");
            return null;
        }
        receipts.put(rs.getString("id"), getReceipt(rs));
        while(rs.next()) {
            receipts.put(rs.getString("id"), getReceipt(rs));
        }
        System.out.println(receipts.size());
        return receipts;
    }
}
