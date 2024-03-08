/**
 * Name : Moulika Maddisetty ; Student Id: 801322113
 */


import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

class Graph {
    public static final int INFINITY = Integer.MAX_VALUE;
    private Map<String, Vertex> vertexMap = new HashMap<>();
    private LinkedList<Edge> unhealthyEdgeList = new LinkedList<>(); //add edges to the linked list
    private TreeSet<String> visited = new TreeSet<>();

    /**
     * If vertexName is not present, add it to vertexMap.
     * In either case, return the Vertex.
     */
    private Vertex getVertex(String vertexName) {
        Vertex v = vertexMap.get(vertexName);
        if (v == null) {
            v = new Vertex(vertexName);
            vertexMap.put(vertexName, v);
        }
        return v;
    }
    /**
     * Reset the variables associated with vertex
     */
    private void resetVertices() {
        for (Vertex v : vertexMap.values()) {
            v.reset();
        }
    }
    /**
     * Change the status of the vertex node to be active & Up in the graph
     */
    private void vertexUp(String name) {
        vertexMap.get(name).status = "up";
    }

    /**
     * Change the status of the vertex node to be disabled & Down in the graph
     */
    private void vertexDown(String name) {
        vertexMap.get(name).status = "down";
    }

    // Changing the edge to active, it is only uni directional
    private void edgeUp(String src, String dest) {
        unhealthyEdgeList.removeIf(edge -> edge.source.vertexName.equals(src) && edge.dest.vertexName.equals(dest));
    }

    // Change the edge to be disabled, and add it to the edgelist which keeps track of disabled edges
    private void edgeDown(String src, String dest) {
        for (Edge edge : unhealthyEdgeList) {
            if (edge.source.vertexName.equals(src) && edge.dest.vertexName.equals(dest)) return;
        }
        unhealthyEdgeList.add(new Edge(getVertex(src), getVertex(dest), 0)); // inserting random distance as we dont check it
    }

    /**
     * Add a new edge to the graph if no edge exists. If there is an edge already update its distance
     */
    public void addEdge(String sourceName, String destName, double distance) {
        Vertex v = getVertex(sourceName);
        Vertex w = getVertex(destName);
        List<Edge> sourceEdges = v.adjEdges;
        int index = 0;
        boolean edgeFound = false;
        for (Edge edge : sourceEdges) {
            if (w.vertexName.equals(edge.dest.vertexName)) {
                v.adjEdges.get(index).dist = distance;
                edgeFound = true;
            }
            index++;
        }
        if (!edgeFound) {
            v.adjEdges.add(new Edge(v, w, distance));
        }
    }

    // Remove an edge from source to destination, and removes the edge from vertex's list
    private void deleteEdge(String src, String dest) {
        Vertex v = getVertex(src);
        List<Edge> adjEdges = v.adjEdges;
        for (int i = 0; i < adjEdges.size(); i++) {
            Edge edge = adjEdges.get(i);
            if (edge.dest.vertexName.equals(dest)) {
                v.adjEdges.remove(i);
                return;
            }
        }
    }
    /**
     * Check for down or disabled edge
     */
    public Boolean isEdgeDown(String src, String dest) {
        for (Edge edge : unhealthyEdgeList) {
            if (edge.source.vertexName.equals(src) && edge.dest.vertexName.equals(dest)) {
                return true;
            }
        }
        return false;
    }

    // Prints the complete graph starting from source vertex in ascending order of names amd appends down when vertex or edge is down
    private void printCompleteGraph() {
        // Since we have to store the vertices in alphabetical order, I used TreeSet & TreeMap
        TreeSet<String> treeSet = new TreeSet<>(vertexMap.keySet()); // Stores vertex names alphabetically
        System.out.println();
        treeSet.forEach(vertex -> {
            System.out.print(vertex);
            if (vertexMap.get(vertex).status.equals("down"))
                System.out.print(" DOWN");
            System.out.println();
            TreeMap<String, Double> neighbours = new TreeMap<>(); // Iteratively review the adjacent neighbours and print them in alphabetic order
            for (Edge edge : vertexMap.get(vertex).adjEdges) {
                neighbours.put(edge.dest.vertexName, edge.dist);
            }
            for (String nextVertex : neighbours.keySet()) {
                System.out.print("  " + nextVertex + " " + neighbours.get(nextVertex));
                if (isEdgeDown(vertex, nextVertex))
                    System.out.print(" DOWN");
                System.out.println();
            }
        });
    }

    // This is a recursive algorithm which is called itself and tracks its adjacent nodes, and is done for all n nodes
    // Complexity is O(logn) for recursive call, for n nodes it would be O(nlogn)
    private void getReachable() {
        TreeSet<String> treeSet = new TreeSet<>();
        for (String vertex : vertexMap.keySet()) {
            if (vertexMap.get(vertex).status.equals("down")) continue;
            treeSet.add(vertex);
        }
        treeSet.forEach(adjVertex -> {
            System.out.print(adjVertex);
            System.out.println();
            visited.clear();
            visited.add(adjVertex);
            isReachable(adjVertex);
            visited.forEach(visitedNode -> {
                if (!visitedNode.equals(adjVertex))
                    System.out.println("  " + visitedNode);
            });
        });
    }

    // Recursive function to add adjacent nodes
    private void isReachable(String adjVertex) {
        TreeSet<String> neighbours = new TreeSet<>();
        for (Edge edge : vertexMap.get(adjVertex).adjEdges) {
            if (edge.dest.status.equals("down")) continue;
            if (isEdgeDown(adjVertex, edge.dest.vertexName)) continue;
            neighbours.add(edge.dest.vertexName);
        }
        for (String node : neighbours) {
            if (!visited.contains(node)) {
                visited.add(node);
                isReachable(node);
            }
        }
    }

    /**
     * Recursive routine to print shortest path. Backtracking the path from destination
     */
    private void printDestinationToSourceVertex(Vertex dest) {
        Vertex previous = dest.prev;
        String name = dest.vertexName;
        if (previous != null) {
            printDestinationToSourceVertex(previous);
            System.out.print(" ");
        }
        System.out.print(name);
    }

    public void printDestinationToSource(String destinationName) {
        Vertex begin = vertexMap.get(destinationName);
        if (begin == null || begin.distance == INFINITY) {
            System.out.println("Destination unreachable");
        } else {
            System.out.println();
            printDestinationToSourceVertex(begin);
            DecimalFormat df = new DecimalFormat("#.##");
            System.out.print(" " + df.format(begin.distance));
            System.out.println();
        }
    }

    //Dijkstras algorithm using the min heap created above
    public void dijkstras(String sourceVertex) {
        resetVertices();
        Heap minPQ = new Heap(vertexMap.size());
        Vertex beginVertex = vertexMap.get(sourceVertex);
        if (beginVertex == null || beginVertex.status.equals("down")) {
            System.out.println("Source vertex is null or disabled. Stopping the traversing");
            return;
        }
        beginVertex.distance = 0.0; //initialize start vertex to 0
        minPQ.insert(new CostNode(beginVertex.vertexName, beginVertex.distance)); //inserting first node to PQ
        while (minPQ.size() != 0) {
            CostNode poppedMinNode = minPQ.extractMin();
            Vertex minVertex = getVertex(poppedMinNode.nodeName); //extract the element at root
            for (Edge edge : minVertex.adjEdges) {  //for every edge in the adjacency list
                Vertex adjacentVertex = edge.dest;
                if (isEdgeDown(minVertex.vertexName, adjacentVertex.vertexName)) //check for disabled edges
                    continue;
                if (adjacentVertex.status.equals("down")) //check for disabled vertices
                    continue;
                double weight = edge.dist;
                if (weight < 0)
                    throw new GraphException("Graph has negative edge");
                if (adjacentVertex.distance > minVertex.distance + weight) {
                    adjacentVertex.distance = minVertex.distance + weight;
                    adjacentVertex.prev = minVertex; //set previous of destination to source which is used for tracking
//                  adjacentVertex.shortestPath = Stream.concat(minVertex.shortestPath.stream(), Stream.of(adjacentVertex)).collect(Collectors.toList());
                    minPQ.insert(new CostNode(adjacentVertex.vertexName, adjacentVertex.distance)); // Updated the priority
                }
            }
        }
    }

    /**
     * Process a request entered by user, To exist please enter "quit" query
     */
    public static boolean processRequest(Scanner sc, Graph g) {
        try {
//          while (!(line = sc.nextLine()).equals("")) { // Inorder to avoid reading empty lines if used file
            String[] queries = sc.nextLine().split(" ");//If the line above is uncommented replace this to String[] queries = line.split(" ");
            switch (queries[0]) {// First word will decide what kind of action we should process
                case "addedge":
                    g.addEdge(queries[1], queries[2], Double.parseDouble(queries[3]));
                    break;
                case "deleteedge":
                    g.deleteEdge(queries[1], queries[2]);
                    break;
                case "edgedown":
                    g.edgeDown(queries[1], queries[2]);
                    break;
                case "edgeup":
                    g.edgeUp(queries[1], queries[2]);
                    break;
                case "vertexdown":
                    g.vertexDown(queries[1]);
                    break;
                case "vertexup":
                    g.vertexUp(queries[1]);
                    break;
                case "path":
                    g.dijkstras(queries[1]);
                    g.printDestinationToSource(queries[2]);
                    break;
                case "print":
                    g.printCompleteGraph();
                    break;
                case "reachable":
                    g.getReachable();
                    break;
                case "quit":
                    return false; //exit query
                default:
                    System.out.println("Not valid query");
                    break;
            }
//       }

        } catch (NoSuchElementException e) {
            System.out.println("Not valid query");
        } catch (GraphException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        try {
            FileReader fin = new FileReader(args[0]);
            Scanner graphFile = new Scanner(fin);

            // Read the edges and insert
            String line;
            while (graphFile.hasNextLine()) {
                line = graphFile.nextLine();
                StringTokenizer st = new StringTokenizer(line);
                try {
                    if (st.countTokens() != 3) {
                        System.err.println("Skipping ill-formatted line " + line);
                        continue;
                    }
                    String source = st.nextToken();
                    String dest = st.nextToken();
                    double distance = Double.parseDouble(st.nextToken());
                    g.addEdge(source, dest, distance);
                    g.addEdge(dest, source, distance);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping ill-formatted line " + line);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        System.out.println("File read...");
        System.out.println(g.vertexMap.size() + " vertices");
        Scanner in = new Scanner(System.in);
        while (processRequest(in, g)) ;
        in.close();
    }
}


/**
 * Edge class which keeps track of distance and direction with source and destination vertices
 * */
class Edge {
    Vertex source;
    Vertex dest;
    double dist;

    Edge(Vertex source, Vertex dest, double dist) {
        this.source = source;
        this.dest = dest;
        this.dist = dist;
    }
}

/**
 * Vertex class with name, edges, the shortest distance, status and reference to the prev node for shortest path
 */
class Vertex {
    String vertexName;
    List<Edge> adjEdges;
    //  List<Vertex> shortestPath;
    String status;
    double distance;
    Vertex prev;

    public Vertex(String nm) {
        vertexName = nm;
        adjEdges = new LinkedList<>();
//      shortestPath = new LinkedList<>();
        status = "up"; //initialize to "UP" status (default status)
        reset();
    }

    public void reset() {
        distance = Graph.INFINITY;
        prev = null;
    }
}

/**
 * CostNode use in creation and transition with Binary Min Heap
 */
class CostNode implements Comparable<CostNode> {
    String nodeName;
    double distance;

    CostNode(String name, Double dist) {
        nodeName = name;
        distance = dist;
    }

    @Override
    public int compareTo(CostNode node) {
        return Double.compare(distance, node.distance);
    }
}

//Implementing a Binary min heap
class Heap {
    CostNode[] costArray;
    int heapSize;
    int maxsize;

    public Heap(int vertextCount) {
        this.heapSize = 0;
        maxsize = vertextCount;
        costArray = new CostNode[this.maxsize + 1];
        costArray[0] = new CostNode("", (double) Integer.MIN_VALUE);
    }

    public int size() {
        return heapSize; //return the size of the Heap
    }

    //function to get right child of a node of a tree
    public int right(int index) {
        return (2 * index) + 1;
    }

    //function to get left child of a node of a tree
    public int left(int index) {
        return ((2 * index));
    }

    //function to get the parent of a node of a tree
    public int parent(int index) {
        return index / 2;
    }

    private boolean isLeaf(int index) {
        return index > (heapSize / 2);
    }

    // Trivial swap function
    private void swap(int i, int j) {
        CostNode temp = costArray[i];
        costArray[i] = costArray[j];
        costArray[j] = temp;
    }

    // Insert a new element at the end of the heap and then adjust its way in the path
    public void insert(CostNode node) {
        if (heapSize >= maxsize)
            return;
        costArray[++heapSize] = node;
        int current = heapSize;
        while (costArray[parent(current)].compareTo(costArray[current]) > 0) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    //Adjust the heap such that the minimum distance is always at the beginning of the array
    private void minHeapify(int index) {
        if (!isLeaf(index)) {
            int minimum;
            // swap with the minimum of the two children to check if right child exists.
            // Otherwise, default value will be '0' which will be swapped with parent node.
            if (right(index) <= heapSize) {
                if (costArray[left(index)].compareTo(costArray[right(index)]) < 0) minimum = left(index);
                else minimum = right(index);
            }
            else
                minimum = left(index);

            if ((costArray[index].compareTo(costArray[left(index)]) > 0) ||
                    costArray[index].compareTo(costArray[right(index)]) > 0) {
                swap(index, minimum);
                minHeapify(minimum);
            }
        }
    }

    // This method extracts the node with minimum distance and adjusts the heap depending on extracted value
    public CostNode extractMin() {
        CostNode root = costArray[1];
        costArray[1] = costArray[heapSize--];
        minHeapify(1); // Adjust the heap again
        return root;
    }
}

// Custom Graph Exception class to print run time failures
class GraphException extends RuntimeException {
    public GraphException(String name) {
        super(name);
    }
}
