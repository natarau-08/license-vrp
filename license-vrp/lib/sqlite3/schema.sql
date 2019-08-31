
-- capacitated vehicle routing problem schema
CREATE TABLE cvrp_graphs (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR (100),
	description VARCHAR (500)
);

CREATE TABLE cvrp_nodes (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	posx REAL, 
	posy REAL, 
	demand INTEGER
);

CREATE TABLE cvrp_arcs (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	node1 INTEGER, 
	node2 INTEGER, 
	cost INTEGER, 
	graph INTEGER,
	
	FOREIGN KEY(node1) REFERENCES cvrp_nodes(id), 
	FOREIGN KEY(node2) REFERENCES cvrp_nodes(id),
	FOREIGN KEY(graph) REFERENCES  cvrp_graphs(id)
);