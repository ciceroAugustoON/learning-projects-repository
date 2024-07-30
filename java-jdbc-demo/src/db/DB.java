package db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {
	private static Connection conn = null;
	
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				conn = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}
	
	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	private static Properties loadProperties() {
		try (FileInputStream fis = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(fis);
			return props;
		} catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	public static boolean schemaLoaded() {
		Properties props = loadProperties();
		String schema = (String)props.get("schemacreated");
		return Boolean.valueOf(schema);
	}
	
	public static void executeSchema(BufferedReader schema) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String str = schema.readLine();
            while (str != null) {
                stringBuilder.append(str);
                str = schema.readLine();
            }
            try (Connection conn = getConnection(); var stmt = conn.createStatement()) {
                stmt.execute(stringBuilder.toString());
                Properties props = loadProperties();
                props.setProperty("schemacreated", "true");
                props.store(new FileOutputStream("db.properties"), null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
