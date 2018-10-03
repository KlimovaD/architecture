package softwarearchs;

import softwarearchs.enums.Role;
import softwarearchs.exceptions.InvalidUser;
import softwarearchs.facade.Facade;
import softwarearchs.gui.*;
import softwarearchs.integration.GetRepair;
import softwarearchs.repair.Invoice;
import softwarearchs.repair.Receipt;
import softwarearchs.user.User;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static Facade facade = new Facade();
    public static User currentUser;
    public static Role currentUserClass;

    public static void main(String[] args) {
        new GetRepair();
        try{
            facade.getAllUsers();
        } catch (InvalidUser e){
            showErrorMessage(e.toString());
            return;
        }
        showSignIn();
    }

    public static void showSignIn(){
        Login login = new Login();
        login.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void showReceiptForm(){
        ReceiptForm receiptForm = new ReceiptForm();
        receiptForm.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void showUsers(boolean window){
        UserInfo userInfo = new UserInfo(window);
        userInfo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void showPayment(Invoice invoice){
        PaymentForm paymentForm = new PaymentForm(invoice);
        paymentForm.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void showInvoices(Receipt currentReceipt){
        InvoiceForm invoiceForm = new InvoiceForm(currentReceipt);
        invoiceForm.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void closeFrame(JFrame frame){
        frame.dispose();
    }

    public static void showErrorMessage(String message){
        JOptionPane.showMessageDialog(new JFrame(),
                message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showInformationMessage(String message){
        JOptionPane.showMessageDialog(new JFrame(),
                message, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static Date dateFromString(String stringDate){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormat.parse(stringDate);
        }catch (ParseException e){
            date = null;
        }
        return date;
    }

    public static String stringFromDate(Date date){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        return dt.format(date);
    }

    public static void frameInit(JFrame frame, JPanel contentPanel, int width, int height){
        frame.setContentPane(contentPanel);
        frame.setLocationRelativeTo(null);
        Dimension size = new Dimension(width, height);
        frame.setSize(size);
        frame.setMaximumSize(size);
        frame.setMinimumSize(size);
    }

    public static int getRowByValue(TableModel model, Object value){
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(value)) {
                return i;
            }
        }
        return -1;
    }
}

