--SELECT * FROM cvrp_graphs;
INSERT INTO cvrp_graphs (name, description, width, height, mdist) VALUES ("example", "testc", 800, 800, 32);

--SELECT * FROM cvrp_nodes;

INSERT INTO cvrp_nodes (posx, posy, demand, graph) VALUES (400, 400, 0 , 1);
INSERT INTO cvrp_nodes (posx, posy, demand, graph) VALUES (720, 50, 37, 1);
INSERT INTO cvrp_nodes (posx, posy, demand, graph) VALUES (20, 650, 35, 1);
INSERT INTO cvrp_nodes (posx, posy, demand, graph) VALUES (780, 540, 30, 1);
INSERT INTO cvrp_nodes (posx, posy, demand, graph) VALUES (150, 635, 25, 1);
INSERT INTO cvrp_nodes (posx, posy, demand, graph) VALUES (320, 166, 32, 1);

--SELECT * FROM cvrp_costs;

INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 1, 2, 28);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 1, 3, 31);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 1, 4, 20);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 1, 5, 25);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 1, 6, 34);

INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 2, 3, 21);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 2, 4, 29);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 2, 5, 26);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 2, 6, 20);

INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 3, 4, 38);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 3, 5, 20);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 3, 6, 22);

INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 4, 5, 30);
INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 4, 6, 27);

INSERT INTO cvrp_costs (graph, node1, node2, val) VALUES (1, 5, 6, 25);

--SELECT * FROM cvrp_graphs;
--SELECT * FROM cvrp_nodes;
--SELECT * FROM cvrp_costs;