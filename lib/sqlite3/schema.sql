
-- capacitated vehicle routing problem schema
CREATE TABLE cvrp_graphs (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR (100),
	description VARCHAR (500)
);

CREATE TABLE cvrp_nodes (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	posx INTEGER, 
	posy INTEGER, 
	demand INTEGER,
	graph INTEGER,
	
	FOREIGN KEY(graph) REFERENCES cvrp_graphs(id)
);

CREATE TABLE cvrp_costs(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	val INTEGER,
	node1 INTEGER,
	node2 INTEGER,
	
	FOREIGN KEY(node1) REFERENCES cvrp_nodes(id), 
	FOREIGN KEY(node2) REFERENCES cvrp_nodes(id)
);

/*
	Reset AUTOINCREMENT:
		DELETE FROM sqlite_sequence WHERE name = 'table_name';
*/