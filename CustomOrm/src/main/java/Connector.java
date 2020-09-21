import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {

    private static Connection connection;
    private static PropertiesGenerator propertiesGenerator;
    private static String url;
    public Connector(String driver, String host, String port, String dbName) {
        url  = String.format("jdbc:%s://%s:%s/%s?autoReconnect=true&useSSL=false",driver,host,port,dbName);
        setPropertiesGenerator();
        setConnection();


    }

    public static void setConnection(){
        try {
            connection = DriverManager.getConnection(url,propertiesGenerator.getProperties());
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void setPropertiesGenerator() {
        propertiesGenerator = new PropertiesGenerator();
    }

    public static Connection getConnection() {
        return  connection;
    }
}
