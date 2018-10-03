package softwarearchs.gui;

import softwarearchs.Main;
import softwarearchs.enums.InvoiceStatus;
import softwarearchs.enums.RepairType;
import softwarearchs.enums.Role;
import softwarearchs.facade.Facade;
import softwarearchs.repair.Invoice;
import softwarearchs.repair.Receipt;
import softwarearchs.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InvoiceForm extends JFrame{
    private JTable invoiceTable;
    private JPanel rootPanel;
    private JTextField invoiceNumber;
    private JTextField dateValue;
    private JTextField priceValue;
    private JTextField clientValue;
    private JTextField receiverValue;
    private JComboBox invoiceStatus;
    private JButton exitButton;
    private JButton newButton;
    private JButton paymentButton;
    private Facade facade = Main.facade;
    private AbstractMap<String, Invoice> invoices= new HashMap<>();
    private Role currentUserClass;
    private User currentUser;
    private Receipt currentReceipt;
    private Invoice selectedInvoice;
    private int clickedRow = -1;

    public InvoiceForm(Receipt currentReceipt){
        this.currentUser = Main.currentUser;
        this.currentUserClass = Main.currentUserClass;
        this.currentReceipt = currentReceipt;

        for(InvoiceStatus status : InvoiceStatus.values())
            invoiceStatus.addItem(status);

        fillTable();
        setInfo();
        setHandlers();
        Main.frameInit(this, rootPanel, 900, 350);
        setVisible(true);
    }

    private void setInfo(){
        if(currentReceipt == null){
            if(invoiceTable.getRowCount() != 0) {
                clickedRow = 0;
                invoiceTable.setRowSelectionInterval(clickedRow, 0);
                rowClicked();
            }
        }else {
            clickedRow = Main.getRowByValue(invoiceTable.getModel(), this.currentReceipt.getReceiptNumber());
            if(clickedRow >= 0) {
                invoiceTable.setRowSelectionInterval(clickedRow, 0);
                rowClicked();
            }
            else {
                newButton.setVisible(true);
                invoiceTable.setVisible(false);
                additionFills();
            }
        }
    }

    private void additionFills(){
        priceValue.setEditable(RepairType.Warranty.equals(currentReceipt.getRepairType()));
        invoiceNumber.setText(currentReceipt.getReceiptNumber());
        dateValue.setText(Main.stringFromDate(new Date()));
        priceValue.setText("150" + 0.0);
        clientValue.setText(currentReceipt.getClient().getFIO());
        receiverValue.setText(currentReceipt.getReceiver().getFIO());
        invoiceStatus.setSelectedItem(InvoiceStatus.Waiting_For_Payment);
    }

    private void setHandlers(){
        exitButton.addActionListener(ev -> Main.closeFrame(this));
        paymentButton.addActionListener(ev ->
            Main.showPayment(selectedInvoice)
        );
        newButton.addActionListener(ev -> {
            priceValue.setEditable(false);
            try {
                selectedInvoice = facade.addInvoice(dateValue.getText(),
                        currentReceipt, priceValue.getText());
            } catch (Exception e) {
                Main.showErrorMessage(e.toString());
                return;
            }

            Main.showInformationMessage("Invoice creation succeeded");
            addTableRow(selectedInvoice);
            paymentButton.setEnabled(true);
        });
        invoiceTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickedRow = invoiceTable.rowAtPoint(e.getPoint());
                if(clickedRow < 0)
                    return;
                rowClicked();
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

    private void rowClicked(){
        TableModel model = invoiceTable.getModel();
        selectedInvoice = invoices.get(model.getValueAt(clickedRow, 0));
        selectedInvoiceInfo(selectedInvoice);
    }

    private void selectedInvoiceInfo(Invoice invoice){
        invoiceNumber.setText(invoice.getInvoiceNumber());
        dateValue.setText(Main.stringFromDate(invoice.getInvoiceDate()));
        priceValue.setText("" + invoice.getPrice());
        clientValue.setText(invoice.getClient().getFIO());
        receiverValue.setText(invoice.getReceiver().getFIO());
        invoiceStatus.setSelectedItem(invoice.getStatus());
        if(invoice.getStatus().equals(InvoiceStatus.Paid))
            paymentButton.setEnabled(false);
        else
            paymentButton.setEnabled(true);
    }

    private void fillTable() {
        invoices.clear();

        if(currentReceipt == null) {
            if (this.currentUserClass.equals(Role.Client)) {
                invoices = facade.getInvoicesByUser(Main.currentUser);
            } else
                invoices = facade.getAllInvoices();
        } else if(currentReceipt != null) {
                Invoice invoice = facade.getInvoice(currentReceipt.getReceiptNumber());
                if(invoice != null)
                    invoices.put(invoice.getInvoiceNumber(), invoice);
        }

        DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
        model.setColumnCount(2);
        Object[] cols = new Object[]{"#", "Status"};
        model.setColumnIdentifiers(cols);
        for (Map.Entry<String, Invoice> receipt : invoices.entrySet())
            model.addRow(new Object[]{receipt.getKey(), receipt.getValue().getStatus()});
        invoiceTable.setModel(model);
    }

    private void addTableRow(Invoice invoice) {
        DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
        model.addRow(new Object[]{invoice.getInvoiceNumber(), invoice.getStatus()});
        invoiceTable.setModel(model);
        invoiceTable.repaint();
    }
}
