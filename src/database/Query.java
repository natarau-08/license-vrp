package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Query {

	private Object[] obs;
	private String sql;
	
	private ResultSet rs = null;
	
	public Query(String sql, Object ...obs) {
		this.obs = obs;
		this.sql = sql;
	}
	
	public void execute() throws Exception{
		rs = SqliteConnection.query(sql, obs);
	}
	
	public void executeWith(Object ...obs) throws Exception {
		this.obs = obs;
		execute();
	}
	
	private void check() throws SQLException{
		if(rs == null) throw new SQLException("Query not executed: " + sql);
	}
	
	public ResultSet getResultSet() throws SQLException {
		check();
		ResultSet temp = rs;
		rs = null;
		return temp;
	}
}
