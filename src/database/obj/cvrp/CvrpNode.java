package database.obj.cvrp;

import java.sql.ResultSet;

import database.SqliteConnection;

public class CvrpNode {

	private int id;
	private int demand;
	private int posX;
	private int posY;
	
	public CvrpNode(int id) throws Exception{
		ResultSet data = SqliteConnection.query("SELECT * FROM cvrp_nodes WHERE id = ?;", id);
		
		this.id = data.getInt("id");
		this.demand = data.getInt("demand");
		this.posX = data.getInt("posx");
		this.posY = data.getInt("posy");
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getDemand() {
		return this.demand;
	}
	
	public int getX() {
		return this.posX;
	}
	
	public int getY() {
		return this.posY;
	}
}
