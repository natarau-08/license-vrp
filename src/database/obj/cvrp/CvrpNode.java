package database.obj.cvrp;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.SqliteConnection;

public class CvrpNode {

	private int id;
	private int demand;
	private int posX;
	private int posY;
	
	public CvrpNode(int id) throws SQLException{
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
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CvrpNode) {
			return ((CvrpNode)obj).id == id;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%d(%d)", id, demand);
	}
	
	
}
