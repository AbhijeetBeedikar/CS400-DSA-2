///////////////////////// TOP OF FILE COMMENT BLOCK ////////////////////////////
//
// Title:           Backend
// Course:          Spring 2025, CS 400
//
// Author:          Riddhi Devarhubli
// Email:           devarhubli@wisc.edu
// Lecturer's Name: Gary Dahl
//
///////////////////////////////// CITATIONS ////////////////////////////////////
//
// no help received
//
/////////////////////////////// 80 COLUMNS WIDE ////////////////////////////////
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * This class implements the methods from the BackendInterface class to find the 
 * shortest path between any two buildings and see how long it takes to walk
 * between any two buildings. This reads the map data from the .dot file.
 */
public class Backend implements BackendInterface{
    private GraphADT<String, Double> graph; 

    /**
     * constructor for this class
     * @param graph the graph used to store and process location and walking time data
     */
    public Backend(GraphADT<String, Double> graph){
        this.graph = graph;
    }
    /**
     * This method adds locations and walking times to the graph data structure. 
     * Buildings are the nodes and the walking times are the edges.
     * @param filename name of the file that the program would read
     * @throws IOException if the file cannot be read
     */
    @Override
    public void loadGraphData(String filename) throws IOException{
        for(String node : new ArrayList<>(graph.getAllNodes())){
            graph.removeNode(node); //remove all nodes and edges
        }
        Scanner scanner = null;

        try{
            scanner = new Scanner(new File(filename));

            while(scanner.hasNextLine()){
                String line = scanner.nextLine().trim();
                if(line.contains("->") && line.contains("[seconds=")){
                    //extracting source node
                    //example: "Memorial Union" -> "Science Hall" [seconds=105.8];
        
                    int firstQuote = line.indexOf('"');//first quote at index 0
                    int secondQuote = line.indexOf('"', firstQuote + 1); // finds the second quote (end of "Memorial Union")
                    //storing the name between the quotes that were just extracted
                    String sourceLocation = line.substring(firstQuote + 1, secondQuote);//+1 allows us to avoid using "

                    //extracting target node
                    int thirdQuote = line.indexOf('"', secondQuote + 1);// finds the next quote (start of "Science Hall")
                    int fourthQuote = line.indexOf('"', thirdQuote + 1); // finds the closing quote for "Science Hall"
                    String destinationLocation = line.substring(thirdQuote + 1, fourthQuote);
                    //extracting weight
                    int secondsStartIndex = line.indexOf("seconds=") + "seconds=".length();
                    int semicolonIndex = line.indexOf(";", secondsStartIndex);
                    String secondsString = line.substring(secondsStartIndex, semicolonIndex).trim();
                    if (secondsString.endsWith("]")) {
                        secondsString = secondsString.substring(0, secondsString.length() - 1);
                    }
                    double travelTimeInSeconds;
                    try {
                        travelTimeInSeconds = Double.parseDouble(secondsString);
                    } catch (NumberFormatException nfe){
                        throw new IOException("Invalid time format in line: \"" + line + "\"", nfe);
                    }//newly updated
                    //insert to graph
                    graph.insertNode(sourceLocation);
                    graph.insertNode(destinationLocation);
                    graph.insertEdge(sourceLocation, destinationLocation, travelTimeInSeconds);
                    
                }
            }
        } catch (FileNotFoundException fnfe) { //added to complete the try-catch blocked used for the method
            throw new IOException("Error reading graph data from file");
        } finally {
            if(scanner != null){
                scanner.close();
            }
        }
    }
    /**
     * This method returns a list of all the building names on the map.
     */
    @Override
    public List<String> getListOfAllLocations(){
        return graph.getAllNodes();
    }
    /**
     * This method allows one to find the shortest walking path between two locations.
     * It returns a list of buildings that one would see while walking between those two 
     * locations. Uses Dijkstra's algorithm.
     * @param startLocation the beginning node
     * @param endLocation the end node
     */
    @Override
    public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
        if (!graph.containsNode(startLocation) || !graph.containsNode(endLocation)) {
            throw new NoSuchElementException("Invalid start/end location: " + startLocation + " → " + endLocation);
        } //newly updated
        try {
            return graph.shortestPathData(startLocation, endLocation);
        } catch (NoSuchElementException e) {
            return new ArrayList<>(); // empty list is returned if there is no path
        }
    }
    /**
     * Similar to the method above, this calculates the shortest path but returns the walking 
     * time between two locations based off the shortest path.
     * @param startLocation the beginning node
     * @param endLocation the end node
     */
    @Override
    public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
        if (!graph.containsNode(startLocation) || !graph.containsNode(endLocation)) {
            throw new NoSuchElementException("Invalid start/end location: " + startLocation + " → " + endLocation);
        } //newly updated
        List<String> path = findLocationsOnShortestPath(startLocation, endLocation);
        List<Double> weights = new ArrayList<>();

        if (path.size() < 2) return weights;

        for (int i = 0; i < path.size() - 1; i++) {
            try {
                weights.add(graph.getEdge(path.get(i), path.get(i + 1)));
            } catch (NoSuchElementException e) {
                return new ArrayList<>(); // broken path
            }
        }


        return weights;
    }
    /**
     * This method finds 10 locations that are closest to the starting point based on shortest paths.
     * @param startLocation the beginning node
     */
    @Override
    public List<String> getTenClosestDestinations(String startLocation) throws NoSuchElementException {
        //check if the input is valid, throw an error
        if (!graph.containsNode(startLocation)) {
            throw new NoSuchElementException("Start location invalid/not found.");
        }

        //hashmap to store reachable destinations
        //the key in the hashmap stores the destination node name and the value (denoted by cost) stores the total walking time in seconds
        Map<String, Double> reachable = new HashMap<>();
        //loop throgh all nodes in the graph
        for (String node : graph.getAllNodes()) {
            //skip the start location, we just want the closest destinations
            if (!node.equals(startLocation)) {
                try {
                    //using Dijkstra's algo
                    //compute the time from start location to the node
                    double cost = graph.shortestPathCost(startLocation, node);
                    //add it to the hashmap
                    reachable.put(node, cost);
                } catch (NoSuchElementException e) {
                    // skip unreachable nodes
                }
            }
        }
        //if the destination is not reachable, throw an error
        if (reachable.isEmpty()) {
            throw new NoSuchElementException("No reachable destinations");
        }
        //process the hashmap:
        //1)reachable.entrySet() gets all (node,cost) pairs
        //2).stream() lets us process them
        //3).sorted() sorts them by coset(which is the value of the pair) so the lowest cost would be the closest
        //4).limit(10) limits it to the 10 closest values only
        //5).map() extracts the keys of the closest values
        //.toList() collects the result of 10 closest as a List<String>
        return reachable.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) //sorts the cost in ascending order
                .limit(10)
                .map(Map.Entry::getKey) //this extracts the destinaton name only
                .toList();
    }


}