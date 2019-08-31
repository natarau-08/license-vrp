package main.obj.cvrp;

import java.util.LinkedList;

import main.database.Query;
import main.database.SqliteConnection;

public class CvrpGraph {
	
	private LinkedList<CvrpArc> arcs;
	
	public CvrpGraph(int index) throws Exception{
		//Query getGraph = new Query();
	}
	
	public static int createNewCvrpGraph(String name, String description) throws Exception{
		
		Query nameQuery = new Query("SELECT COUNT(*) AS c FROM cvrp_graphs WHERE name LIKE ?;", name);
		nameQuery.execute();
		
		if (nameQuery.getResultSet().getInt("c") != 0) {
			throw new Exception("There is already a graph named " + name);
		}
		
		SqliteConnection.query("INSERT INTO cvrp_graphs (name, description) VALUES(?, ?);", name, description);
		
		nameQuery.execute();
		
		if (nameQuery.getResultSet().getInt("c") == 0) {
			throw new Exception("Cannot create graph " + name);
		}
		
		return -1;
	}
}
