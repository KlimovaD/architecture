package softwarearchs.storage;

import java.sql.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Gateway {

    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String mysql_url = "jdbc:mysql://localhost:3306/service___center?autoReconnect=true&useSSL=false";
    private static final String mysql_user = "root";
    private static final String mysql_password = "root";
    private static MysqlDataSource dataSource;

    private static Gateway gateway;

    public Gateway () throws SQLException{
        dataSource = new MysqlDataSource();
        dataSource.setURL(mysql_url);
        dataSource.setUser(mysql_user);
        dataSource.setPassword(mysql_password);

        try{
            Class.forName(JDBC_DRIVER);
        } catch(ClassNotFoundException e){
            System.out.println(e.toString());
        }
    }

    public static Gateway getGateway() throws SQLException{
        if(gateway == null)
            gateway = new Gateway();
        return gateway;
    }

    public static Connection getConnection(){
        Connection result = null;
        try{
            result = dataSource.getConnection();
        } catch(SQLException e){
            System.out.println(e.toString());
        }
        return result;
    }
}
