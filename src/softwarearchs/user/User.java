package softwarearchs.user;

//import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import softwarearchs.enums.Role;
import softwarearchs.exceptions.InvalidSignIn;
import softwarearchs.exceptions.InvalidUser;
import softwarearchs.facade.Facade;
import softwarearchs.integration.emailNotification.Notifications;
import softwarearchs.repair.Receipt;
import softwarearchs.storage.MapperRepository;

import javax.mail.internet.AddressException;

public abstract class User {

    /* User information */
    protected int id;
    protected String name;
    protected String surname;
    protected String patronymic;

    protected String phoneNumber;
    protected String eMail;

    protected String login;

    public User(int id, String name, String surname, String patronymic, String login) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;

        this.login = login;
    }

    public User(String name, String surname, String patronymic, String login) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;

        this.login = login;
    }

    public int getId() { return this.id; }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public String getPatronymic() {
        return patronymic;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String geteMail() {
        return eMail;
    }
    public String getLogin() {
        return login;
    }
    public String getFIO() { return this.name + " " + this.surname + " " + this.patronymic; }

    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
    public void setLogin(String login) {
        this.login = login;
    }


    public boolean registrationNotification(String pwd) throws AddressException{
        Notifications nots = new Notifications(login, getFIO(), eMail, pwd);
        if(!nots.verify("registration"))
            throw new AddressException("Invalid email address");

        return true;
    }

    public Role getClassName(){
        return Role.valueOf(this.getClass().getSimpleName());
    }

    public static User signIn(String login, String pwd) throws InvalidSignIn, InvalidUser{
        (new MapperRepository()).signIn(login, pwd);
        return (new MapperRepository()).findUser(login);
    }
}
