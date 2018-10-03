package softwarearchs.storage.mapper;

import softwarearchs.Main;
import softwarearchs.additional.BankAccount;
import softwarearchs.storage.Gateway;
import softwarearchs.user.Client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BankMapper {

    private static AbstractMap<String, BankAccount> accounts = new HashMap<>();

    public boolean addAccount(BankAccount account) throws SQLException {

        if(findAccount(account.getAccountNumber()) != null )
            return false;

        String statement = "INSERT INTO bankaccount VALUES (" + account.getAccountNumber() +
                ", " + account.getClient() + ", DATE \'" + Main.stringFromDate(account.getValidTill()) +
                "\', " + account.getCvc() + ", " + account.getBalance() + ");";

        PreparedStatement insert = Gateway.getGateway().getConnection().prepareStatement(statement);
        insert.execute();
        insert.execute();
        accounts.put(account.getAccountNumber(), account);
        return true;
    }

    public static BankAccount findAccount(String accountNumber) throws SQLException{
        if(accounts.containsKey(accountNumber))
            return accounts.get(accountNumber);

        String statement = "SELECT * FROM bankaccount WHERE id = \"" + accountNumber +"\";";
        PreparedStatement find = Gateway.getGateway().getConnection().prepareStatement(statement);
        find.execute();
        ResultSet rs = find.executeQuery();

        if(!rs.next()) return null;

        Client client = (Client)UserMapper.findUser(rs.getInt("Client"));
        Date validDate = rs.getDate("ValidDate");
        int cvc = rs.getInt("CVC");
        BankAccount account = new BankAccount(accountNumber, client, validDate, cvc);
        account.setBalance(rs.getDouble("Balance"));

        accounts.put(accountNumber, account);
        return account;
    }

    public static boolean updateAccount(BankAccount account) throws SQLException{
        String statement = "UPDATE bankaccount SET Balance = " + account.getBalance()
                + " WHERE id = \"" + account.getAccountNumber() + "\";";
        PreparedStatement updateStatement = Gateway.getGateway().getConnection().prepareStatement(statement);
        if(updateStatement.executeUpdate() == 0)
            return false;

        accounts.replace(account.getAccountNumber(), account);
        return true;
    }
}
