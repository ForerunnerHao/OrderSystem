package menu.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLOpenHelper {
    // volatile keyword Ensure variable modification is visible to all threads
    private static volatile MySQLOpenHelper instance = null;
    private static HikariDataSource ds;

    private MySQLOpenHelper() {
        restartConnectionPool();
    }

    public void restartConnectionPool(){
        Properties props = new Properties();
        InputStream in = MySQLOpenHelper.class.getResourceAsStream("/hikari.properties");
        if (in == null) {
            throw new RuntimeException("Cannot find hikari.properties in classpath");
        }
        try {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load hikari.properties", e);
        }
        HikariConfig config = new HikariConfig(props);
        ds = new HikariDataSource(config);
    }

    public static MySQLOpenHelper getInstance() {
        if (instance == null) {
            // Double-Checked Locking: guarantee thread safety
            synchronized (MySQLOpenHelper.class) {
                if (instance == null) {
                    instance = new MySQLOpenHelper();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void closeDataSource() {
        if (ds != null) {
            ds.close();
        }
    }
}
