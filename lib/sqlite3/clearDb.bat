@echo off

SET /P cnf=Are you sure (Y/N)? This will clear all tables 
IF /I "%cnf%" NEQ "Y" GOTO END 

sqlite3.exe db.sq3 "DELETE FROM cvrp_graphs;"
sqlite3.exe db.sq3 "DELETE FROM cvrp_nodes;"
sqlite3.exe db.sq3 "DELETE FROM cvrp_costs;"

sqlite3.exe db.sq3 "DELETE FROM sqlite_sequence WHERE name='cvrp_nodes';"
sqlite3.exe db.sq3 "DELETE FROM sqlite_sequence WHERE name='cvrp_costs';"
sqlite3.exe db.sq3 "DELETE FROM sqlite_sequence WHERE name='cvrp_graphs';"

echo Done

GOTO DONE

:END
	echo No changes were made

:DONE
	

pause