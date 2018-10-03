package softwarearchs.storage.mapper;

import softwarearchs.enums.Role;
import softwarearchs.exceptions.CreationFailed;
import softwarearchs.exceptions.InvalidPaymentData;
import softwarearchs.exceptions.InvalidSignIn;
import softwarearchs.exceptions.InvalidUser;
import softwarearchs.storage.Gateway;
import softwarearchs.user.Client;
import softwarearchs.user.Master;
import softwarearchs.user.Receiver;
import softwarearchs.user.User;

import java.sql.*;
import java.util.*;

public class UserMapper {

    private static AbstractMap<String, User> users = new HashMap<>();

    public boolean addUser(User user, String pwd) throws SQLException, InvalidUser, CreationFailed{
        try{
            findUser(user.getLogin());
        }catch (InvalidUser e){
            String statement = "INSERT INTO users(Name, Surname, Patronymic, PhoneNumber, " +
                    "Email, Login, Password, Role) VALUES (\"" + user.getName() +
                    "\", \"" + user.getSurname() + "\", \"" + user.getPatronymic() +
                    "\", \"" + user.getPhoneNumber() + "\", \"" + user.geteMail() +
                    "\", \"" + user.getLogin() + "\", \"" + pwd +
                    "\", \"" + user.getClass().getSimpleName() + "\");";
            PreparedStatement insert = Gateway.getGateway().getConnection().prepareStatement(statement);
            insert.execute();
            user = findUser(user.getLogin());
            if (user == null)
                throw new InvalidUser("User not found");
            users.put(user.getLogin(), user);
            return true;
        }

        throw new CreationFailed("User already exists");

    }

    private static User getUser(ResultSet rs) throws SQLException{
        User user;
        switch (Role.valueOf(rs.getString("Role"))){
            case Receiver:
                user = new Receiver(rs.getInt("id"), rs.getString("Name"),
                        rs.getString("Surname"), rs.getString("Patronymic"),
                        rs.getString("Login"));
                break;
            case  Master:
                user = new Master(rs.getInt("id"), rs.getString("Name"),
                        rs.getString("Surname"), rs.getString("Patronymic"),
                        rs.getString("Login"));
                break;
            case Client:
                user = new Client(rs.getInt("id"), rs.getString("Name"),
                        rs.getString("Surname"), rs.getString("Patronymic"),
                        rs.getString("Login"));
                break;
            default:
                user = null;
                break;
        }
        if(user == null) return null;

        user.seteMail(rs.getString("Email"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));

        return user;
    }

    public static boolean deleteUser(String login) throws SQLException, InvalidUser{
        findUser(login);
        users.remove(login);
        String statement = "DELETE FROM users WHERE Login = \"" + login + "\";";

        PreparedStatement insert = Gateway.getGateway().getConnection().prepareStatement(statement);
        insert.execute();
        return true;
    }

    public static User findUser(String login)  throws SQLException, InvalidUser{
        if(users.containsKey(login))
            return users.get(login);

        String statement = "SELECT * from users WHERE Login = \"" + login + "\";";
        PreparedStatement find = Gateway.getGateway().getConnection().prepareStatement(statement);
        ResultSet rs = find.executeQuery();
        if (!rs.next())
            throw new InvalidUser("User not found");
        User user = getUser(rs);

        if(user == null)
            throw new InvalidUser("User not found");

        users.put(login, user);
        return user;
    }

    public static User findUser(String name, String surname, String patronymic)
            throws SQLException{

        for (User user : users.values()){
            if (name.equals(user.getName()) && surname.equals(user.getSurname())
                    && patronymic.equals(user.getPatronymic()))
                return user;
        }

        String statement = "SELECT * from users WHERE Name = \"" + name +
                "\" AND Surname = \"" + surname +
                "\" AND Patronymic =\"" + patronymic + "\";";
        PreparedStatement find = Gateway.getGateway().getConnection().prepareStatement(statement);
        ResultSet rs = find.executeQuery();
        if (!rs.next()) return null;
        User user = getUser(rs);

        if(user == null) return null;

        users.put(user.getLogin(), user);
        return user;
    }

    public static User findUser(int id)  throws SQLException{
        for (User user : users.values()){
            if (id == user.getId())
                return user;
        }

        String statement = "SELECT * FROM users WHERE id = " + id + ";";
        PreparedStatement find = Gateway.getGateway().getConnection().prepareStatement(statement);
        ResultSet rs = find.executeQuery();
        if (!rs.next()) return null;
        User user = getUser(rs);

        if(user == null)
            return null;

        users.put(user.getLogin(), user);
        return user;
    }

    public static AbstractMap<String, User> findAll() throws SQLException, InvalidUser{
        users.clear();
        String query = "SELECT * FROM users;";
        Statement statement = Gateway.getGateway().getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);

        while(rs.next())
            users.put(rs.getString("Login"), findUser(rs.getString("Login")));

        return users;
    }

    public boolean updateUser(User user) throws SQLException, InvalidUser {

        String statement = "UPDATE users SET Name = \"" + user.getName()
                + "\", Surname = \"" + user.getSurname()
                + "\", Patronymic = \"" + user.getPatronymic()
                + "\", PhoneNumber = \"" + user.getPhoneNumber()
                + "\", Email = \"" + user.geteMail()
                + "\", Role = \"" + user.getClass().getSimpleName()
                + "\" WHERE Login = \"" + user.getLogin() + "\";";
        PreparedStatement updateStatement = Gateway.getGateway().getConnection().
                prepareStatement(statement);
        if(updateStatement.executeUpdate() == 0)
            throw new InvalidUser("User was not updated");

        users.replace(user.getLogin(), user);
        return true;
    }

    public boolean signIn(String login, String pwd) throws SQLException, InvalidSignIn{
        String statement = "SELECT * FROM users WHERE Login = \"" + login + "\";";
        PreparedStatement find = Gateway.getGateway().getConnection().prepareStatement(statement);
        ResultSet rs = find.executeQuery();

        if(!rs.next() && rs.getString("Password").equals(pwd))
            throw new InvalidSignIn("Invalid login or password");

        return true;
    }

}
