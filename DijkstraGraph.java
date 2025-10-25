// === CS400 File Header Information ===
// Name: Abhijeet Beedikar
// Email: beedikar@wisc.edu
// Group and Team: <your group name: two letters, and team color>
// Group TA: <name of your group's ta>
// Lecturer: Gary Dahl
// Notes to Grader: <optional extra notes>

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;


/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     * <p>
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;

        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {super(new HashtableMap<>());}

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *                                or when either start or end data do not
     *                                correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        // implement in step 5.3
        HashtableMap<NodeType,SearchNode> visited = new HashtableMap<>();
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();

        //the cost of going from the start node to the start node is 0
        pq.add(new SearchNode(nodes.get(start), 0.0, null));

        while (!pq.isEmpty()) {
            // Traversing through the next node with the highest priority (Cheapest total path length)
            SearchNode a = pq.remove();
            if (visited.containsKey(a.node.data)) continue;
            if (a.node.data.equals(end)) return a; // If the next node is the end node then the searchNode found contains the shortest path to the destination

            visited.put(a.node.data, a);
            // Add all edges from the current node of interest into the priority queue so that we can choose the next shortest path to traverse down
            for (Edge i : a.node.edgesLeaving) {
                if (!visited.containsKey(i.successor.data)) { // ensures that visited nodes are not added to the priority queue
                    pq.add(new SearchNode(i.successor, a.cost + i.data.doubleValue(), a));
                }
            }
        }
        // if no value is returned and the priority queue is empty, we can assume that we never
        // came across the end node during our traversal, thus we can automatically throw an exception here
        throw new NoSuchElementException("There is no path that connects start node to destination node");
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        // implement in step 5.4
        try {
            // traversing through the path from the destination node to the start node
            ArrayList<NodeType> shortestPath = new ArrayList<>();
            SearchNode shortest = computeShortestPath(start, end);
            while (shortest.cost > 0) {
                shortestPath.add(shortest.node.data);
                shortest = shortest.predecessor;
            }
            shortestPath.add(shortest.node.data);

            //ArrayList is reversed because we added elements along the path in reverse order
            Collections.reverse(shortestPath);

            return shortestPath;
        } catch (NoSuchElementException e) { //No path exists
            throw new NoSuchElementException("There is no path that connects start node to destination node");
        }
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path freom the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        // implement in step 5.4
        return computeShortestPath(start, end).cost;
    }

    /**
     * Test that makes use of an example traced through in lecture.
     * Confirms that the results of the implementation matches what was previously computed by hand.
     */
    @Test
    public void dijkstraTest1() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");
        graph.insertNode("I");
        graph.insertNode("L");
        graph.insertNode("M");
        graph.insertEdge("A", "B", 1);
        graph.insertEdge("A", "H", 7);
        graph.insertEdge("A", "M", 5);

        graph.insertEdge("B", "M", 3);

        graph.insertEdge("D", "F", 4);
        graph.insertEdge("D", "G", 2);
        graph.insertEdge("D", "A", 7);

        graph.insertEdge("F", "G", 9);

        graph.insertEdge("G", "H", 9);
        graph.insertEdge("G", "L", 7);
        graph.insertEdge("G", "A", 4);

        graph.insertEdge("H", "B", 6);
        graph.insertEdge("H", "I", 2);
        graph.insertEdge("H", "L", 2);

        graph.insertEdge("I", "H", 2);
        graph.insertEdge("I", "D", 1);

        graph.insertEdge("M", "I", 4);
        graph.insertEdge("M", "E", 3);
        graph.insertEdge("M", "F", 4);

        //checking whether every edge between two nodes exists in our graph as intended
        assertTrue(graph.getEdge("A", "B").equals(1));
        assertTrue(graph.getEdge("A", "H").equals(7));
        assertTrue(graph.getEdge("A", "M").equals(5));

        assertTrue(graph.getEdge("B", "M").equals(3));

        assertTrue(graph.getEdge("D", "F").equals(4));
        assertTrue(graph.getEdge("D", "G").equals(2));
        assertTrue(graph.getEdge("D", "A").equals(7));

        assertTrue(graph.getEdge("F", "G").equals(9));

        assertTrue(graph.getEdge("G", "H").equals(9));
        assertTrue(graph.getEdge("G", "L").equals(7));
        assertTrue(graph.getEdge("G", "A").equals(4));

        assertTrue(graph.getEdge("H", "B").equals(6));
        assertTrue(graph.getEdge("H", "I").equals(2));
        assertTrue(graph.getEdge("H", "L").equals(2));

        assertTrue(graph.getEdge("I", "H").equals(2));
        assertTrue(graph.getEdge("I", "D").equals(1));

        assertTrue(graph.getEdge("M", "I").equals(4));
        assertTrue(graph.getEdge("M", "E").equals(3));
        assertTrue(graph.getEdge("M", "F").equals(4));
    }

    /**
     * Test that check the cost and sequence of data along the shortest path
     * between a different start and end node.
     */
    @Test
    public void dijkstraTest2() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");
        graph.insertNode("I");
        graph.insertNode("L");
        graph.insertNode("M");
        graph.insertEdge("A", "B", 1);
        graph.insertEdge("A", "H", 7);
        graph.insertEdge("A", "M", 5);

        graph.insertEdge("B", "M", 3);

        graph.insertEdge("D", "F", 4);
        graph.insertEdge("D", "G", 2);
        graph.insertEdge("D", "A", 7);

        graph.insertEdge("F", "G", 9);

        graph.insertEdge("G", "H", 9);
        graph.insertEdge("G", "L", 7);
        graph.insertEdge("G", "A", 4);

        graph.insertEdge("H", "B", 6);
        graph.insertEdge("H", "I", 2);
        graph.insertEdge("H", "L", 2);

        graph.insertEdge("I", "H", 2);
        graph.insertEdge("I", "D", 1);

        graph.insertEdge("M", "I", 4);
        graph.insertEdge("M", "E", 3);
        graph.insertEdge("M", "F", 4);

        assertTrue(graph.shortestPathCost("D", "I") == 13.0);
        try {
            //May throw an IndexOutOfBoundsError is the method is faulty
            String expected = graph.shortestPathData("D", "I").get(0) +
                    graph.shortestPathData("D", "I").get(1) +
                    graph.shortestPathData("D", "I").get(2) +
                    graph.shortestPathData("D", "I").get(3);
            assertTrue(expected.equals("DGHI"));
        } catch (IndexOutOfBoundsException e) {
            assertTrue(false, "Less than expected nodes");
        }
    }

    /**
     * Test that checks the behavior of the implementation when there is
     * no sequence of directed edges that connects the start and end nodes.
     */
    @Test
    public void dijkstraTest3() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");
        graph.insertNode("I");
        graph.insertNode("L");
        graph.insertNode("M");
        graph.insertEdge("A", "B", 1);
        graph.insertEdge("A", "H", 7);
        graph.insertEdge("A", "M", 5);

        graph.insertEdge("B", "M", 3);

        graph.insertEdge("D", "F", 4);
        graph.insertEdge("D", "G", 2);
        graph.insertEdge("D", "A", 7);

        graph.insertEdge("F", "G", 9);

        graph.insertEdge("G", "H", 9);
        graph.insertEdge("G", "L", 7);
        graph.insertEdge("G", "A", 4);

        graph.insertEdge("H", "B", 6);
        graph.insertEdge("H", "I", 2);
        graph.insertEdge("H", "L", 2);

        graph.insertEdge("I", "H", 2);

        graph.insertEdge("M", "I", 4);
        graph.insertEdge("M", "E", 3);
        graph.insertEdge("M", "F", 4);
        String start = "I";
        String end = "D";
        if (graph.containsNode(start) && graph.containsNode(end)) {
            try {
                //computeShortestPath() must throw an exception if there is no path from start to end node
                graph.computeShortestPath(start, end);
                assertTrue(false, "computeShortestPath() did not call NoSuchElementException when there was no path between start and end");
            } catch (NoSuchElementException e) {
                assertTrue(true);
            }
        }
        else {
            assertTrue(false, "start and end nodes are not contained in graph");
        }
    }
}
