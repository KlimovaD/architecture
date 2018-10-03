package softwarearchs.integration.emailNotification;

import softwarearchs.repair.Receipt;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import java.util.Properties;

public class Notifications {
    private String login;
    private String name;
    private String email;
    private String password;

    private Receipt receipt;

    private Properties props = new Properties();

    private final String fromEmail = "Servicecentersoftwarearc@gmail.com";
    private final String emailPassword = "Servicecentersoftwarearc2018";

    public Notifications(String login, String name, String email, String password) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.password = password;

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    public Notifications(Receipt receipt){
        this.receipt = receipt;
        this.name = receipt.getClient().getFIO();
        this.email = receipt.getClient().geteMail();
        this.login = receipt.getClient().getLogin();
        this.password = "Secret";

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    public boolean verify(String type) {
        if (!isValidEmailAddress()) return false;
        return "registration".equals(type)? sendWelcome() : sendStatusChanging();
    }

    private boolean isValidEmailAddress() {
        try {
            new InternetAddress(email).validate();
        } catch (AddressException ex) {
            return false;
        }
        String hostname = email.split("@")[1];
        try {
            return SendEmail.doMailServerLookup(hostname);
        } catch (NamingException e) {
            return false;
        }
    }

    private boolean sendWelcome() {
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, emailPassword);
            }
        };
        Session session = Session.getInstance(props, auth);

        StringBuilder sb = new StringBuilder();
        sb.append("Dear " + name + "!\n");
        sb.append("We are glad to report about successful registration at our service center.\n");
        sb.append("Your user data:\n");
        sb.append("Login: " + login + "\n");
        sb.append("Name: " + name + "\n");
        sb.append("Password: " + password + "\n");
        sb.append("\nBest regards\n");

        SendEmail.sendEmail(session, email,"Welocme!", sb.toString());
        return true;
    }

    private boolean sendStatusChanging(){
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, emailPassword);
            }
        };
        Session session = Session.getInstance(props, auth);

        StringBuilder sb = new StringBuilder();
        sb.append("Dear " + name + "!\n");
        sb.append("We are glad to report about repair status chnging.\n");
        sb.append("Receipt # is: " + receipt.getReceiptNumber() + "\n");
        sb.append("Device: "  + receipt.getDevice().getDeviceBrand() + " " +
                receipt.getDevice().getDeviceModel() + " " + receipt.getDevice().getSerialNumber() + "\n");
        sb.append("New status: " + receipt.getStatus().toString() + "\n");
        sb.append("Your master: " + (receipt.getMaster() == null ? "Master has not assigned yet" :
                receipt.getMaster().getFIO()) + "\n");
        sb.append("Repair type: " + receipt.getRepairType() + "\n");
        sb.append("\nBest regards\n");

        SendEmail.sendEmail(session, email,"Repair status changed", sb.toString());
        return true;
    }
}
