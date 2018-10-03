package softwarearchs.user;

import softwarearchs.enums.ReceiptStatus;
import softwarearchs.exceptions.AcessPermision;
import softwarearchs.integration.emailNotification.Notifications;
import softwarearchs.repair.Receipt;

import javax.mail.internet.AddressException;

public class Client extends User {
    public Client(int id, String name, String surname, String patronymic, String login){
        super(id, name, surname, patronymic, login);
    }

    public Client(String name, String surname, String patronymic, String login){
        super(name, surname, patronymic, login);
    }

    public boolean statusChangingNotification(Receipt receipt) throws AddressException {
        Notifications nots = new Notifications(receipt);
        if(!nots.verify("changing"))
            throw new AddressException("Invalid email address");

        return true;
    }
}
