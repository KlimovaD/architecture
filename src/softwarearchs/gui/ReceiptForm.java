package softwarearchs.gui;

import softwarearchs.Main;
import softwarearchs.exceptions.InvalidUser;
import softwarearchs.repair.Device;
import softwarearchs.enums.InvoiceStatus;
import softwarearchs.enums.ReceiptStatus;
import softwarearchs.enums.RepairType;
import softwarearchs.enums.Role;
import softwarearchs.facade.Facade;
import softwarearchs.repair.Invoice;
import softwarearchs.repair.Receipt;
import softwarearchs.user.Client;
import softwarearchs.user.Master;
import softwarearchs.user.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.*;

import static softwarearchs.enums.Role.Master;
import static softwarearchs.enums.Role.Receiver;

public class ReceiptForm extends JFrame {
    private JPanel rootPanel;
    private JTable receipts;
    private JTextField receiptNumber;
    private JTextField receiptDate;
    private JComboBox repairType;
    private JTextField deviceSerial;
    private JTextField deviceType;
    private JTextField deviceBrand;
    private JTextField deviceModel;
    private JTextField devicePurchaseDate;
    private JTextField deviceWarrantyExpiration;
    private JTextField devicePreviousRepair;
    private JTextField deviceRepairWarrantyExpiration;
    private JTextField clientName;
    private JTextField clientSurname;
    private JTextField clientPatronymic;
    private JTextField clientPhoneNumber;
    private JTextField clientEmail;
    private JTextArea deviceMalfunction;
    private JTextArea deviceNote;
    private JTextField receiver;
    private JTextField master;
    private JComboBox receiptStatus;
    private JButton exitButton;
    private JButton updateButton;
    private JButton newButton;
    private JButton userInfoButton;
    private JButton findDevice;
    private JButton findClient;
    private JButton assignYourselfButton;
    private JButton createInvoiceButton;
    private JButton showInvoicesButton;


    private User currentUser;
    private Receipt selectedReceipt = null;
    private Facade facade = Main.facade;
    private String newRec = "New";
    private String addRec = "Add receipt";
    private String exitButtonCaption = "Exit";
    private String cancelCaption = "Cancel";
    private String updateCaption = "Update";
    private String commitCaption = "Commit changes";
    private Role currentUserClass;
    private int clickedRow = -1;

    public ReceiptForm(){
        Main.frameInit(this,rootPanel, 1250, 500);
        setVisible(true);

        if(repairType.getItemCount() == 0)
            for (RepairType type : RepairType.values())
                repairType.addItem(type);

        if(receiptStatus.getItemCount() == 0)
            for(ReceiptStatus status : ReceiptStatus.values())
                receiptStatus.addItem(status);

        this.currentUser = Main.currentUser;
        this.currentUserClass = Main.currentUserClass;
        if(Receiver.equals(currentUserClass)){
            newButton.setVisible(true);
            newButton.setEnabled(true);
            createInvoiceButton.setVisible(true);
        }

        fillTable();
        setupHandlers();
    }

    private void masterPermissions(boolean state){
        repairType.setEditable(state);
        receiptStatus.setEditable(state);
        repairType.setEnabled(state);
        receiptStatus.setEnabled(state);
        deviceMalfunction.setEditable(state);
        deviceNote.setEditable(state);
        master.setEditable(master.getText().isEmpty());
        assignYourselfButton.setVisible(master.getText().isEmpty());
        assignYourselfButton.setEnabled(master.getText().isEmpty());
    }

    private void receiverPermissions(boolean state, String type){
        if("all".equals(type)) {
            deviceSerial.setEditable(state);
            deviceType.setEditable(state);
            deviceBrand.setEditable(state);
            deviceModel.setEditable(state);
            devicePurchaseDate.setEditable(state);
            deviceWarrantyExpiration.setEditable(state);
            devicePreviousRepair.setEditable(state);
            deviceRepairWarrantyExpiration.setEditable(state);
            clientName.setEditable(state);
            clientSurname.setEditable(state);
            clientPatronymic.setEditable(state);
            clientPhoneNumber.setEditable(state);
            clientEmail.setEditable(state);
            findClient.setVisible(state);
            findDevice.setVisible(state);
        }
        repairType.setEnabled(state);
        deviceMalfunction.setEditable(state);
        deviceNote.setEditable(state);
        receiptStatus.setEnabled(state);
    }

    private void clearElements(){
        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dt1 = new SimpleDateFormat("hhmmss");
        Date today = new Date();
        receiptNumber.setText(dt.format(today) + dt1.format(today) + currentUser.getId());
        receiptDate.setText(Main.stringFromDate(today));
        repairType.setSelectedIndex(-1);
        deviceSerial.setText("");
        deviceType.setText("");
        deviceBrand.setText("");
        deviceModel.setText("");
        devicePurchaseDate.setText("");
        deviceWarrantyExpiration.setText("");
        devicePreviousRepair.setText("");
        deviceRepairWarrantyExpiration.setText("");
        clientName.setText("");
        clientSurname.setText("");
        clientPatronymic.setText("");
        clientPhoneNumber.setText("");
        clientEmail.setText("");
        deviceMalfunction.setText("");
        deviceNote.setText("");
        receiptStatus.setSelectedIndex(-1);
        master.setText("");
        receiver.setText(currentUser.getFIO());
    }

    private boolean receiptAdded() {
        if (isReceiptAdditionDataValid()) {
            Main.showErrorMessage("Fill in all fields");
            return false;
        }
        String warrantyExp = deviceWarrantyExpiration.getText();
        String repWarrantyExp = deviceRepairWarrantyExpiration.getText();


        Receipt receipt;
        try {
            String clientFIO = clientName.getText() + " " + clientSurname.getText()
                    + " " + clientPatronymic.getText();
            Device device = facade.getReceiptDevice(deviceSerial.getText(), deviceType.getText(),
                    deviceBrand.getText(), deviceModel.getText(), clientFIO, devicePurchaseDate.getText(),
                    warrantyExp, devicePreviousRepair.getText(), repWarrantyExp);

            receipt = facade.addReceipt(receiptNumber.getText(), receiptDate.getText(),
                    repairType.getSelectedItem().toString(), device, deviceMalfunction.getText(),
                    deviceNote.getText(), receiptStatus.getSelectedItem().toString());
        }catch(InvalidUser e) {
            int output = JOptionPane.showConfirmDialog(rootPanel
                    , "Client not found. Create new user?"
                    , "Question"
                    ,JOptionPane.YES_NO_OPTION);

            if(output == JOptionPane.YES_OPTION){
                Main.showUsers(true);
                return false;
            } else {
                return false;
            }
        }catch(Exception e){
            Main.showErrorMessage(e.getMessage());
            return false;
        }

        addTableRow(receipt);
        return true;
    }

    private void setupHandlers(){
        ReceiptForm thisFrame = this;
        exitButton.addActionListener(ev -> exitButtonAction(thisFrame));
        newButton.addActionListener(ev -> newButtonAction());
        findDevice.addActionListener(e -> findDeviceAction());
        findClient.addActionListener(e -> findClientAction());
        updateButton.addActionListener(e -> updateButtonAction());
        assignYourselfButton.addActionListener(e -> master.setText(currentUser.getFIO()));
        createInvoiceButton.addActionListener(ev -> Main.showInvoices(selectedReceipt));
        showInvoicesButton.addActionListener(ev -> Main.showInvoices(selectedReceipt));
        userInfoButton.addActionListener(e -> {
            Main.showUsers(true);
            Main.closeFrame(thisFrame);
        });
        receipts.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickedRow = receipts.rowAtPoint(e.getPoint());
                if(clickedRow < 0) return;
                TableModel model = receipts.getModel();

                try {
                    selectedReceipt = facade.getReceipt(
                            model.getValueAt(clickedRow, 0).toString());
                } catch (Exception exc){
                    Main.showErrorMessage(exc.toString());
                }
                    selectedReceiptInfo(selectedReceipt);
            }

            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void fillTable(){
        DefaultTableModel model = (DefaultTableModel) receipts.getModel();
        model.setColumnCount(2);
        Object[] cols = new Object[]{"Receipt", "Status"};
        model.setColumnIdentifiers(cols);
        for(Map.Entry<String, Receipt> receipt : facade.getByUser(currentUser).entrySet())
            model.addRow(new Object[]{receipt.getKey(), receipt.getValue().getStatus()});
        receipts.setModel(model);
    }

    private void addTableRow(Receipt receipt){
        DefaultTableModel model = (DefaultTableModel)receipts.getModel();
        model.addRow(new Object[]{receipt.getReceiptNumber(), receipt.getStatus()});
        receipts.setModel(model);
        receipts.repaint();
    }

    private void replaceRow(Receipt receipt){
        TableModel model = receipts.getModel();
        model.setValueAt(receipt.getReceiptNumber(), clickedRow, 0);
        model.setValueAt(receipt.getStatus(), clickedRow, 1);
        receipts.setModel(model);
        receipts.repaint();
    }

    private void selectedReceiptInfo(Receipt receipt){
        ActionListener als [] = showInvoicesButton.getActionListeners();
        for(ActionListener al : als)
            showInvoicesButton.removeActionListener(al);

        if(newButton.getText().equals(newRec)) {
            receiptNumber.setText(receipt.getReceiptNumber());
            receiptDate.setText(Main.stringFromDate(receipt.getReceiptDate()));
            repairType.setSelectedItem(receipt.getRepairType());
            devicePreviousRepair.setText(
                    receipt.getDevice().getPrevRepair() == null ? ""
                            : Main.stringFromDate(receipt.getDevice().getPrevRepair()));
            deviceRepairWarrantyExpiration.setText(
                    receipt.getDevice().getRepairWarrantyExpiration() == null ? ""
                            : Main.stringFromDate(receipt.getDevice().getRepairWarrantyExpiration()));
            if(Receiver.equals(currentUserClass)
                    || Master.equals(currentUserClass)) {
                updateButton.setVisible(!receipt.isClosed());
                updateButton.setEnabled(!receipt.isClosed());
            }
            receiptStatus.setSelectedItem(receipt.getStatus());
            createInvoiceButton.setEnabled(false);

            showInvoicesButton.setText("All invoices");

            if(receiptStatus.getSelectedItem().toString().equals(ReceiptStatus.Closed.toString()) ||
                    receiptStatus.getSelectedItem().toString().equals(ReceiptStatus.Ready_for_extr.toString())){
                if(facade.getInvoice(receipt.getReceiptNumber()) != null){
                    createInvoiceButton.setEnabled(false);
                    showInvoicesButton.setText("Current invoice");
                    showInvoicesButton.addActionListener(ev -> Main.showInvoices(receipt));
                } else{
                    createInvoiceButton.setEnabled(true);
                    showInvoicesButton.addActionListener(ev -> Main.showInvoices(null));
                }
            }

        }
        if(newButton.getText().equals(addRec)){
            devicePreviousRepair.setText(receipt.getReceiptDate().toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(receipt.getReceiptDate());
            calendar.add(Calendar.MONTH, 1);
            deviceRepairWarrantyExpiration.setText(Main.stringFromDate(calendar.getTime()));
        }
        deviceSerial.setText(receipt.getDevice().getSerialNumber());
        deviceType.setText(receipt.getDevice().getDeviceType());
        deviceBrand.setText(receipt.getDevice().getDeviceBrand());
        deviceModel.setText(receipt.getDevice().getDeviceModel());
        devicePurchaseDate.setText(
                receipt.getDevice().getDateOfPurchase() == null ? ""
                        : Main.stringFromDate(receipt.getDevice().getDateOfPurchase()));
        deviceWarrantyExpiration.setText(
                receipt.getDevice().getWarrantyExpiration()== null ? ""
                        : Main.stringFromDate(receipt.getDevice().getWarrantyExpiration()));

        clientName.setText(receipt.getClient().getName());
        clientSurname.setText(receipt.getClient().getSurname());
        clientPatronymic.setText(receipt.getClient().getPatronymic());
        clientPhoneNumber.setText(receipt.getClient().getPhoneNumber());
        clientEmail.setText(receipt.getClient().geteMail());

        deviceMalfunction.setText(receipt.getMalfuncDescr());
        deviceNote.setText(receipt.getNote());

        receiver.setText(receipt.getReceiver().getFIO());
        master.setText(receipt.getMaster() == null ? "" : receipt.getMaster().getFIO());
    }

    private boolean isReceiptAdditionDataValid(){
        return repairType.getSelectedIndex() == -1 || deviceSerial.getText().isEmpty() ||
                deviceType.getText().isEmpty() || deviceBrand.getText().isEmpty() ||
                deviceModel.getText().isEmpty() || deviceMalfunction.getText().isEmpty() ||
                clientName.getText().isEmpty() || clientSurname.getText().isEmpty() ||
                clientPhoneNumber.getText().isEmpty() || clientEmail.getText().isEmpty();
    }

    private void newButtonAction(){
        if(newRec.equals(newButton.getText())) {
            clearElements();
            receiverPermissions(true, "all");
            updateButton.setEnabled(false);
            receiptStatus.setEnabled(false);
            receiptStatus.setSelectedItem(ReceiptStatus.Opened);
            newButton.setText(addRec);
            exitButton.setText(cancelCaption);
            findClient.setVisible(true);
            findDevice.setVisible(true);
            showInvoicesButton.setEnabled(false);
            createInvoiceButton.setEnabled(false);

            return;
        }
        if(addRec.equals(newButton.getText())){
            receiverPermissions(false, "all");
            if(!receiptAdded()) {
                Main.showErrorMessage("Failure during adding receipt");
                return;
            }
            else
                Main.showInformationMessage("New receipt was successfully added");

            findDevice.setVisible(false);
            findClient.setVisible(false);
            newButton.setText(newRec);
        }
    }

    private void exitButtonAction(JFrame frame){
        if(exitButtonCaption.equals(exitButton.getText())){
            Main.showSignIn();
            Main.closeFrame(frame);
        } else {
            clearElements();
            receiverPermissions(false, "all");
            masterPermissions(false);
            updateButton.setText(updateCaption);
            updateButton.setEnabled(false);
            showInvoicesButton.setEnabled(true);
            newButton.setText(newRec);
            newButton.setEnabled(true);
            exitButton.setText(exitButtonCaption);
        }
    }

    private void findDeviceAction() {
        if (deviceSerial.getText().isEmpty()) {
            Main.showErrorMessage("Serial is empty");
            return;
        }
        Device device = facade.getDevice(deviceSerial.getText());
        if (device == null) {
            Main.showErrorMessage("Device not found");
            return;
        }

        deviceType.setText(device.getDeviceType());
        deviceBrand.setText(device.getDeviceBrand());
        deviceModel.setText(device.getDeviceModel());
        devicePurchaseDate.setText(
                device.getDateOfPurchase() == null ? "" :
                        Main.stringFromDate(device.getDateOfPurchase()));
        deviceWarrantyExpiration.setText(
                device.getWarrantyExpiration() == null ? "" :
                        Main.stringFromDate(device.getWarrantyExpiration()));
        devicePreviousRepair.setText(
                device.getPrevRepair() == null ? "" :
                        Main.stringFromDate(device.getPrevRepair()));
        deviceRepairWarrantyExpiration.setText(
                device.getRepairWarrantyExpiration() == null ? "" :
                        Main.stringFromDate(device.getRepairWarrantyExpiration()));

        clientName.setText(device.getClient().getName());
        clientSurname.setText(device.getClient().getSurname());
        clientPatronymic.setText(device.getClient().getPatronymic());
        clientEmail.setText(device.getClient().geteMail());
        clientPhoneNumber.setText(device.getClient().getPhoneNumber());
    }

    private void findClientAction(){
        if(clientName.getText().isEmpty()){
            Main.showErrorMessage("Login is empty. Search failed");
            return;
        }
        Client client;
        try {
            client = (Client) facade.getUser(clientName.getText());
        } catch(InvalidUser e){
            int output = JOptionPane.showConfirmDialog(rootPanel
                    , "Client not found. Create new user?"
                    , "Question"
                    ,JOptionPane.YES_NO_OPTION);

            if(output == JOptionPane.YES_OPTION){
                Main.showUsers(true);
                return;
            } else {
                return;
            }
        }

        clientName.setText(client.getName());
        clientSurname.setText(client.getSurname());
        clientPatronymic.setText(client.getPatronymic());
        clientEmail.setText(client.geteMail());
        clientPhoneNumber.setText(client.getPhoneNumber());
    }

    private void updateButtonAction(){
        if(updateCaption.equals(updateButton.getText())){
            if(Master.equals(currentUserClass))
                masterPermissions(true);

            if(Receiver.equals(currentUserClass))
                receiverPermissions(true, "update");

            newButton.setEnabled(false);
            showInvoicesButton.setEnabled(false);
            createInvoiceButton.setEnabled(false);
            updateButton.setText(commitCaption);
            exitButton.setText(cancelCaption);
            return;
        }
        if(commitCaption.equals(updateButton.getText())){
            if(Master.equals(currentUserClass) && master.getText().isEmpty()){
                Main.showErrorMessage("Assign master on this receipt");
                return;
            }

            String status = receiptStatus.getSelectedItem().toString();
            try {
                selectedReceipt = facade.setReceiptStatus(status, selectedReceipt);
                selectedReceipt.setRepairType(RepairType.valueOf(repairType.getSelectedItem().toString()));
                selectedReceipt.setStatus(ReceiptStatus.valueOf(receiptStatus.getSelectedItem().toString()));
                selectedReceipt.setMalfuncDescr(deviceMalfunction.getText());
                selectedReceipt.setNote(deviceNote.getText());

                selectedReceipt = facade.assignOnRepair(selectedReceipt);
                facade.updateReceipt(selectedReceipt);

            }catch(Exception e){
                Main.showErrorMessage(e.toString());
                return;
            }

            replaceRow(selectedReceipt);
            Main.showInformationMessage("Receipt was updated");
            masterPermissions(false);
            receiverPermissions(false, "all");
            showInvoicesButton.setEnabled(true);
            updateButton.setText(updateCaption);
            exitButton.setText(exitButtonCaption);
        }
    }
}
