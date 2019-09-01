package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteManager {

	public static void clearDatabase() throws SQLException{
		ResultSet tables = SqliteConnection.query("SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE '%sqlite%';");
		
		while (tables.next()) {
			String tableName = tables.getString("name");
			String query = "DELETE FROM " + tableName + ";";
			
			SqliteConnection.query(query);
			SqliteConnection.query("DELETE FROM sqlite_sequence WHERE name = ?;", tableName);
		}
		
		SqliteConnection.query("VACUUM;");
	}
}
