/*   
Name: Moulika Maddisetty
Student ID: 801322113
Mail ID: mmaddise@uncc.edu
ITCS-6114/8114: Algorithms and Data Structures
Project 2: ReadMe
 */


Introduction:
This java file contains implementation of an open shortest path first using Dijkstra's shortest path algorithm. 

Language:
The code is written in Java, the minimum version required is java 8

Compiler:
To compile this program, need jdk installed and setup. Can complie this using an IDE or through console.
Using IDE open the project eg. in Intellij IDE and click on the build project (or Ctrl +F9)
If using a command line use the command javac Graph.java

How to execute:
There are two ways to execute this. Using files to read standard input and write to standard output:
1. To execute the using file in command line use the command : 
  java Graph.java networkFile < queries.txt file > output.txt
2. To execute without using the file, simply run the program like
   java Graph.java networkFile
This will give an empty prompt inorder for the user to enter commands. Manually type in commands like
    print
    path Belk Education etc..
To execute the file using intellij, use the run button in the top right corner of the IDE

Data Structures Used:
I have used some interesting data structures in this project:
- HashMap : 
To keep track of vertices information while running the program
- LinkedList : 
Used this data structure to keep track of all the unhealthy edges in the graph
- TreeSet : 
This is used whenever I needed an ordered set of elements in the program
- TreeMap: 
It is also used for ordered set of elements, but as key value pairs whenever a lookups are necessary
- Binary Min Heap : 
This is implemented without using any libraries and used as a priority queue for implementing Dijkstra's

What Works?
- This algorithm works well for all the positive edges scenario
- It adheres to the format of the input file for network information and builds graph accordingly
- Program correctly responds to all changes in the graph like edges up& down, vertices up& down etc..
- Reachable algorithm accurately tracks all the available & reachable nodes and edges
- Shortest path is efficiently determined even if the edges are up/down
- If there are negative distances the shortest path may not be effectively calculated
- If there is no quit at the end of the queries.txt while using file command(java Graph.java networkFile < queries.txt file > output.txt) it may run for all the empty lines 
- or
- Uncomment the while loop if it is included to stop reading empty lines before reading queries.txt need to be entered without a new line

Algorithms:

Two important algorithms in the program:
- Dijkstra's Shortest Path Algorithm 
- Reachable vertex algorithm
Pseudo code for Dijkstra's:
Identify a source vertex inorder to start the algorithm
   fun dijkstra(Graph, beginVertex){
     dist[source] <- 0
     for(vertex v: Graph.vertices) {
            dist[v] <- INFINITY
            prev[v] <- undefined
            add v to the minPQ
          }
     while(minPQ is not empty){
            u <- Get the vertex with min dist[u] and remove u from minPQ
            for each neighbour w of u, which are in minPQ
                altDistance <- dist[u] + Graph.EdgeDist(u, w)
                if altDistance < dist[w]:
                    dist[w] <- altDistance 
                    prev[w] <- u
          }
     return dist[] prev[]
     }
Pseudo code for Reachable vertex:

ALGORITHM checkReachable( )
 Sort all the vertices in alphabetical order using TreeSet
 for each vertex which is "up" in the list
 		Print vertexName
		Add the vertex to visited nodes list
		Call reachable(vertex) for every node
	End for
	Print all the Vertices from visited_nodes in alphabetical order
  	ALGORITHM reachable(vertex)
  	 FOR each adjacent vertices
  			if edge is down or vertex is down then
  				continue;
  			end if
  			if Vertex exists in visited nodes list then
  				continue
  			else
  				Add vertex to visited node list
  				Call reachable(vertex)
  			for if
  		End for

Time Complexity:

The Time complexity of dijkstra's algorithm is O((V+E)logV). Where E stands for the total edges and V stands for total vertices
The Time complexity of Reachable algorithm is O(nlogn). It takes O(logn) to get the reachable vertices of a node, since this is called for all n nodes it is O(nlogn)

Structure Of Code:

The Program contains the following classes :
1. Edge Class : This contains the source vertex, destination and the distance between them
2. Vertex Class: This class contains information about the vertex, prev vertex which the shortest path is coming from, shortest distance and edges
3. CostNode : This class is used to maintain and track heap contents to maintain min heap
4. GraphException : This is custom exception class used for printing run time exceptions
5. Heap: This class is the implementation of Binary Min Heap
6. Graph : This is the main class which stores input graph and contains all the methods for changing the graph 
The Graph class has some helper methods which help in altering the graph's health 
- Changes to Vertex : Helper methods to bring up or down the vertex
- Changes to Edges : Helper methods to up or down an edge, add or remove an edge
- Reachable Vertices : Finds reachable vertices for every vertex in the graph
- Finding shortest path: Finds the short path between source and destination
- Print Methods: Useful print methods to print the status of the graph
