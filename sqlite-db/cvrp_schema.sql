
-- capacitated vehicle routing problem schema
CREATE TABLE cvrp_graph (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT,
	description TEXT,
	width INTEGER,
	height INTEGER,
	
	UNIQUE(name)
);

CREATE TABLE cvrp_node (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	x INTEGER, 
	y INTEGER, 
	demand INTEGER,
	graph INTEGER,
	
	FOREIGN KEY(graph) REFERENCES cvrp_graph (id)
);

-- sqlite does not support adding FKs after table creation
CREATE TABLE cvrp_graph_depot (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	graph INTEGER,
	node INTEGER,
	
	FOREIGN KEY(graph) REFERENCES cvrp_graph(id),
	FOREIGN KEY(node) REFERENCES cvrp_node(id)
);

CREATE TABLE cvrp_cost (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	graph INTEGER,
	val INTEGER,
	node1 INTEGER,
	node2 INTEGER,
	
	FOREIGN KEY(node1) REFERENCES cvrp_nodes(id), 
	FOREIGN KEY(node2) REFERENCES cvrp_nodes(id)
);

-- SELECT * FROM cvrp_graphs;

--DROP TABLE cvrp_graphs;
--DROP TABLE cvrp_costs;