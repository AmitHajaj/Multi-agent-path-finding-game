![(level 23, with 3 agents)](https://www.emp-online.com/dw/image/v2/BBQV_PRD/on/demandware.static/-/Library-Sites-EMPSharedLibrary/en/dw62ac5d38/images/bands/pok_mon.jpg?sw=1400)
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

# Strategy
As said, our target is to plan the agent moves to be as efficient as possible, means, eat as many pokemons as possible in given time, and also importent to not make more then 10 moves per second.

To achieve this goal I decided to go with this strategy:
  1. When starting the game we need to assign each agent a node to start from. So we go threw all over the pokemon's and we choose the n-th valuest pokemons, when n is the            number of agent's. Finally we put each agent on the source node of this n-th pokemons's edges they sitt on.
  2. Now, when each agent has a node to start with, we can start the game. While the game is running, whenever an agent is on a node(not moving) we check from the                    available(*2.1) pokemon's list which one is the most worth catching(*2.2). When we find one we find the shortest path to him with dijkstra's algorithm and follow this path.
     Moreoveer, if there is two or more pokemon's on the same edge, we update the value of the path to the sum of this pokemon's value. and compute 2.1.
     
     Note: If an agent is at node that is a source node for a pokemon, it will choose him without further searching.
     
     *2.1 available pokemon is one that there is no agent who targetized him. When agent targetize a pokemon he change his availabillity flag to false.
     
     *2.2 The decision of worth catching is computed with the following formula: (weight of the shortest path)/(value of this pokemon or the sum of the pokemons on this edge).
  3. Important to say, when there is a case that two pokemon's on the same edge and there is two different agent's on the way to them, in future search one of them will take          ownership on both of them and the other agent will be needed to search a different pokemon.
   
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





