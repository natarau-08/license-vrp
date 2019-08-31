package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqliteConnection {

	/***
	 * Database url
	 */
	public static final String URL = "jdbc:sqlite:lib/sqlite3/db.sq3";
	
	private static Connection connection;
	
	public static void init() {
		try {
			connection = DriverManager.getConnection(URL);
			
			checkDatabase();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static PreparedStatement prepare(String query) {
		try {
			return connection.prepareStatement(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ResultSet query(String sql, Object ...obj) {
		try {
			PreparedStatement stm = prepare(sql);
			int k = 1;
			for(Object o : obj) {
				if(o instanceof Integer) {
					stm.setInt(k, (int) o);
					k++;
					continue;
				}
				
				if(o instanceof String ) {
					stm.setString(k, (String)o);
					k++;
					continue;
				}
				
				if(o instanceof Boolean) {
					stm.setInt(k, ((Boolean)o ? 1 : 0));
					k++;
					continue;
				}
				
				if(o instanceof Double) {
					stm.setString(k, "" + (double)o);
					k++;
					continue;
				}
				
				if(o instanceof Float) {
					stm.setFloat(k, (float)o);
					k++;
					continue;
				}
				
				throw new Exception("Unregistered type " + o.getClass().getName());
			}
			
			stm.execute();
			
			return stm.getResultSet();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void checkDatabase() throws Exception{
		//TODO
	}
	
}
