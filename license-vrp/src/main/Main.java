package main;

import main.database.SqliteConnection;
import main.obj.cvrp.CvrpGraph;

public class Main {

	public static void main(String args[]) {
		SqliteConnection.init();
		try {
			CvrpGraph.createNewCvrpGraph("test", "debug rtest");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
