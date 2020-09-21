import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesGenerator {
    private Properties properties;
    public PropertiesGenerator(){
        this.properties = new Properties();
        this.properties.setProperty("user","root");
        this.properties.setProperty("password","1234");
    }

    public Properties getProperties() {
        return properties;
    }
}
