package softwarearchs.gui;

import softwarearchs.Main;
import softwarearchs.enums.InvoiceStatus;
import softwarearchs.facade.Facade;
import softwarearchs.repair.Invoice;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;

public class PaymentForm extends JFrame{
    private JPanel rootPanel;
    private JPasswordField cvcValue;
    private JTextField validationDateValue;
    private JTextField accountNumberValue;
    private JTextField clientValue;
    private JTextField invoiceNumberValue;
    private JTextField priceValue;
    private JTextField receiverValue;
    private JButton payButton;
    private JCheckBox paymentType;
    private JButton exitButton;

    private Facade facade = Main.facade;
    private Invoice currentInvoice;

    public PaymentForm(Invoice currentInvoice){
        this.currentInvoice = currentInvoice;

        Main.frameInit(this, rootPanel, 600, 250);
        setInfo();
        setHandler();
        setVisible(true);
    }

    private void setInfo(){
        invoiceNumberValue.setText(currentInvoice.getInvoiceNumber());
        priceValue.setText("" + currentInvoice.getPrice());
        receiverValue.setText(currentInvoice.getReceiver().getFIO());
        clientValue.setText(currentInvoice.getClient().getFIO());
    }

    private void setHandler(){
        exitButton.addActionListener(ev -> Main.closeFrame(this));
        paymentType.addActionListener(ev -> {
            JCheckBox cb = (JCheckBox) ev.getSource();
            if(cb.isSelected()){
                paymentType(false);
            } else{
                paymentType(true);
            }
        });
        accountNumberValue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {}
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lostFocus(focusEvent);
            }
        });
        validationDateValue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {}
            @Override
            public void focusLost(FocusEvent focusEvent) {
                lostFocus(focusEvent);
            }
        });
        cvcValue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {}
            @Override
            public void focusLost(FocusEvent focusEvent) { lostFocus(focusEvent); }
        });
        payButton.addActionListener(ev -> payAction());
    }

    private void lostFocus(FocusEvent ev) {
        if (!ev.isTemporary()) {
            if (paymentType.isSelected())
                return;

            if (new String(cvcValue.getPassword()).isEmpty() ||
                    accountNumberValue.getText().isEmpty() ||
                    validationDateValue.getText().isEmpty()) {
                payButton.setEnabled(false);
                return;
            }
            payButton.setEnabled(true);
        }
    }

    private void paymentType(boolean state){
        accountNumberValue.setEnabled(state);
        validationDateValue.setEnabled(state);
        cvcValue.setEnabled(state);
        payButton.setEnabled(!state);
    }

    private void payAction(){
        if(paymentType.isSelected()) {
            int output = JOptionPane.showConfirmDialog(rootPanel
                    , "Cash payment?"
                    , "Question"
                    , JOptionPane.YES_NO_OPTION);

            if (output == JOptionPane.YES_OPTION) {
                Main.showInformationMessage("Cash payment succeed");
                currentInvoice.setStatus(InvoiceStatus.Paid);
                try {
                    facade.updateInvoice(currentInvoice);
                } catch (Exception e){
                    Main.showErrorMessage(e.toString());
                    return;
                }
                return;
            } else {
                return;
            }
        }

        Date validDate = Main.dateFromString(validationDateValue.getText());
        try {
            facade.payForRepair(accountNumberValue.getText(), validDate,
                    new String(cvcValue.getPassword()), clientValue.getText(), currentInvoice);
            facade.updateInvoice(currentInvoice);
        } catch(Exception e){
            Main.showErrorMessage(e.getMessage());
            return;
        }
        Main.showInformationMessage("Payment successful");
        Main.closeFrame(this);
    }
}
