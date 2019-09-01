SELECT * FROM sqlite_master
WHERE type = 'table'
AND name NOT LIKE 'sqlite_%';

SELECT * FROM sqlite_sequence;

SELECT * FROM cvrp_graphs;

SELECT * FROM cvrp_nodes
WHERE graph = 1;

SELECT * FROM cvrp_costs cc
JOIN cvrp_nodes cn ON cc.node1 = cn.id
WHERE cn.graph = 1;

SELECT * FROM cvrp_nodes

SELECT COUNT(*) FROM cvrp_nodes;

SELECT COUNT(*) FROM cvrp_costs;