# OOP-course-game
Game based on pacman. Uses graph theory algorithm. This game has two parts, the first one is for the directed weighted graph implementation and algorithms on a graph.
The second is for managing the game, via GUI and the game algorithm

## First part- section 1
As said the first part implements a directed weighted graph and algorithms we can use on them.
Our graph build from nodes and edges thet each one of them implemented as an inner class inside the graph class. moreover we have another inner class that represent geographic location in 3D space.( x axis, y axis, z axis) 

## First part- section 2
In the second part we have an algorithms that we can use on the graph. with this class, one can check whether a graph is connected or not, find the shortest path between two nodes and ofcourse the shortest path distance between two nodes.
For checking if graph is connected we use DFS algorithm on a directed weighted graph. For finding the shortest path and the shortest path distance we use the famous dijkstra's algorithm. 

## Second part
Here we implements the game itself. to do so we created an Agent object, Pokemon object, Arena object and GUI.
Agent- in the game, to eat as many much pokemon as possible in a givet time. for every pokemon he eats he get point based on his value. The agent speed increases for every pokemon he eats.
Pokemon- each pokemon has a value, and constant location on some random edge.
Arena- this object gives takes the graph which comes with the chosen level, and takes data from Agents and Pokemons to fit them in the graph. each move we refresh the arena based on the current data.
GUI- we use JFrame and JPanel and takes the geographic location of each component of the game(graph, agents and pokemons) and draw it on the frame.

# How to play?
There is an exe file which opens by double click on it. before the game start, it asks for the user's ID and a level to play.(currently, 0-23) 
each level has a given graph, number of agents and time to play. 

# Usage
Suitable for PC's. the game based on Java. 

## Libraries
annotations-13.0
Ex2_Server_v0.13
gson-2.8.2
java-json
JUnit 4/5.4
Kotlin-stdlib-1.3.72
Kotlin-stdlib-common-1.3.701
okhttp-4.8.0
okio-2.7.0





