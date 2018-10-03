package softwarearchs.storage.mapper;

import softwarearchs.Main;
import softwarearchs.exceptions.CreationFailed;
import softwarearchs.repair.Device;
import softwarearchs.storage.Gateway;
import softwarearchs.user.Client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DeviceMapper {

    private static AbstractMap<String, Device> devices = new HashMap<>();
    private static Date today = new Date();

    public boolean addDevice(Device device) throws SQLException, CreationFailed {

        if(findDevice(device.getSerialNumber()) != null)
            throw new CreationFailed("Device already exists");

        String dateOfPurchase = device.getDateOfPurchase() == null ?
                "NULL" : "DATE \'" + Main.stringFromDate(device.getDateOfPurchase()) + "\'";
        String warrantyExpir = device.getWarrantyExpiration() == null ?
                "NULL" : "DATE \'" + Main.stringFromDate(device.getWarrantyExpiration()) + "\'";
        String prevRepair = device.getPrevRepair() == null ?
                "NULL" : "DATE \'" + Main.stringFromDate(device.getPrevRepair()) + "\'";
        String repairWarranty = device.getRepairWarrantyExpiration() == null ?
                "NULL" : "DATE \'" + Main.stringFromDate(device.getRepairWarrantyExpiration()) + "\'";

        String statement = "INSERT INTO device VALUES (\"" + device.getSerialNumber() +
                "\", \"" + device.getDeviceType() + "\", \"" + device.getDeviceBrand() +
                "\", \"" + device.getDeviceModel() + "\", " + dateOfPurchase +
                ", " + warrantyExpir + ", " + prevRepair + ", " + repairWarranty + ", "
                + device.getClient().getId() + ");";
        PreparedStatement insert = Gateway.getGateway().getConnection().prepareStatement(statement);
        insert.execute();
        devices.put(device.getSerialNumber(), device);
        return true;
    }

    public static boolean updateDevice(Device device) throws SQLException{
        String purchase = device.getDateOfPurchase() == null ?
                "NULL" : "DATE \'" + Main.stringFromDate(device.getDateOfPurchase()) + "\'";
        String warrantyExp = device.getWarrantyExpiration() == null ?
                "NULL" : "DATE \'" + Main.stringFromDate(device.getWarrantyExpiration()) + "\'";
        String prevRep = device.getPrevRepair() == null ?
                "NULL" : "DATE \'" + Main.stringFromDate(device.getPrevRepair()) + "\'";
        String repWarranty = device.getRepairWarrantyExpiration() == null ?
                "NULL" : "DATE \'" + Main.stringFromDate(device.getRepairWarrantyExpiration()) + "\'";

        String statement = "UPDATE device SET Purchase = " + purchase + ", WarrantyExpiration = "
                + warrantyExp + ", PreviousRepair = " + prevRep + ", RepairWarrantyExpiration = "
                + repWarranty + " WHERE SerialNumber = \"" + device.getSerialNumber() + "\";";
        PreparedStatement updateStatement = Gateway.getGateway().getConnection()
                .prepareStatement(statement);
        if(updateStatement.executeUpdate() == 0)
            return false;

        devices.replace(device.getSerialNumber(), device);
        return true;
    }

    public static Device findDevice(String serialNumber) throws SQLException{

        if(devices.containsKey(serialNumber))
            return devices.get(serialNumber);

        String statement = "SELECT * FROM device WHERE SerialNumber = \"" + serialNumber + "\";";
        PreparedStatement find = Gateway.getGateway().getConnection().prepareStatement(statement);
        ResultSet rs = find.executeQuery();

        if(!rs.next()) return null;

        Device device = new Device(rs.getString("SerialNumber"),
                rs.getString("DeviceType"), rs.getString("Brand"),
                rs.getString("Model"), (Client)UserMapper.findUser(rs.getInt("Client")));

        device.setDateOfPurchase(rs.getDate("Purchase"));
        device.setWarrantyExpiration(rs.getDate("WarrantyExpiration"));
        device.setPrevRepair(rs.getDate("PreviousRepair"));
        device.setRepairWarrantyExpiration(rs.getDate("RepairWarrantyExpiration"));
        if(device.getWarrantyExpiration() != null) {
            device.setWarrantyAvailable(
                    device.getWarrantyExpiration().after(today) ||
                            device.getWarrantyExpiration().equals(today)
            );
        }
        if(device.getRepairWarrantyExpiration() != null) {
            device.setRepairWarrantyAvailable(
                    device.getRepairWarrantyExpiration().after(today) ||
                            device.getRepairWarrantyExpiration().equals(today)
            );
        }
        devices.put(device.getSerialNumber(), device);
        return device;
    }

}
