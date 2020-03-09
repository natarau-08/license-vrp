
-- capacitated vehicle routing problem schema
CREATE TABLE cvrp_graph (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT,
	description TEXT,
	width INTEGER NOT NULL,
	height INTEGER NOT NULL,
	node_diameter INTEGER NOT NULL,
	margin INTEGER NOT NULL,
	
	UNIQUE(name)
);

CREATE TABLE cvrp_node (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	x INTEGER NOT NULL, 
	y INTEGER NOT NULL, 
	demand INTEGER NOT NULL,
	graph INTEGER NOT NULL,
	
	FOREIGN KEY(graph) REFERENCES cvrp_graph (id)
);

-- sqlite does not support adding FKs after table creation
CREATE TABLE cvrp_graph_depot (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	graph INTEGER NOT NULL,
	node INTEGER NOT NULL,
	
	FOREIGN KEY(graph) REFERENCES cvrp_graph(id),
	FOREIGN KEY(node) REFERENCES cvrp_node(id)
);

CREATE TABLE cvrp_cost (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	graph INTEGER NOT NULL,
	val INTEGER NOT NULL,
	node1 INTEGER NOT NULL,
	node2 INTEGER NOT NULL,
	
	FOREIGN KEY(node1) REFERENCES cvrp_nodes(id), 
	FOREIGN KEY(node2) REFERENCES cvrp_nodes(id)
);