import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;

import db.DB;

public class Application {

	public static void main(String[] args) {
		Connection conn = DB.getConnection();
		if (!DB.schemaLoaded()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader("schema.sql"));
				DB.executeSchema(br);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		DB.closeConnection();
	}

}
