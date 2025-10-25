import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Used to fetch HTML Fragments used to develop the Frontend
 */
public class Frontend implements FrontendInterface {
    BackendInterface backend;


    /**
     * Implementing classes should support the constructor below.
     *
     * @param backend is used for shortest path computations
     */
    public Frontend(BackendInterface backend) {
        this.backend = backend;
        try {
            backend.loadGraphData("campus.dot");
        } catch (IOException e) {
            System.err.println("Could not load graph data.");
            e.printStackTrace();
        }
    }


    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a text input field with the id="start", for the start location
     * - a text input field with the id="end", for the destination
     * - a button labelled "Find Shortest Path" to request this computation
     * Ensure that these text fields are clearly labelled, so that the user
     * can understand how to use them.
     *
     * @return an HTML string that contains input controls that the user can
     * make use of to request a shortest path computation
     */
    public String generateShortestPathPromptHTML() {


        return "<input type=\"text\" id=\"start\"></input> start location\n" + // Input box for the start location to be entered


                "<input type=\"text\" id=\"end\"></input> destination\n" + // Input box for the destination to be entered


                "<input type=\"button\" value=\"Find Shortest Path\"></input>"; //Button to initiate the process of finding the shortest path


    }
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a paragraph (p) that describes the path's start and end locations
     * - an ordered list (ol) of locations along that shortest path
     * - a paragraph (p) that includes the total travel time along this path
     * Or if there is no such path, the HTML returned should instead indicate
     * the kind of problem encountered.
     *
     * @param start is the starting location to find a shortest path from
     * @param end   is the destination that this shortest path should end at
     * @return an HTML string that describes the shortest path between these
     * two locations
     */
    public String generateShortestPathResponseHTML(String start, String end) {
        List<String> shortestPath = backend.findLocationsOnShortestPath(start, end);
        if (!shortestPath.isEmpty()) {
            String output = "<p> Start Location: " + start + "; End Location: " + end + "</p>\n";


            // create an ordered list of all locations along the shortest path 
            output += "<ol>\n";
            for (String i : shortestPath)
                output += "<li>" + i + "</li>\n";
            output += "</ol>\n";


            // calculates and returns total travel time along shortest Path
            Double totalTime = 0.0;
            for (Double i : backend.findTimesOnShortestPath(start, end)) totalTime += i;
            return output + "<p> Total Travel Time: " + totalTime + " seconds </p> \n";
        } else {
            return "<p>No such path exists</p>\n";
        }
    }


    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a text input field with the id="from", for the start location
     * - a button labelled "Ten Closest Destinations" to submit this request
     * Ensure that this text field is clearly labelled, so that the user
     * can understand how to use it.
     *
     * @return an HTML string that contains input controls that the user can
     * make use of to request a ten closest destinations calculation
     */
    public String generateTenClosestDestinationsPromptHTML() {
        return "<input type=\"text\" id=\"from\"></input> Start Location \n" +
                "<input type=\"button\" value=\"Ten Closest Destinations\"></input>";
    }


    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a paragraph (p) that describes the start location that travel time to
     * the closest destinations are being measured from
     * - an unordered list (ul) of the ten locations that are closest to start
     * Or if no such destinations can be found, the HTML returned should
     * instead indicate the kind of problem encountered.
     *
     * @param start is the starting location to find close destinations from
     * @return an HTML string that describes the closest destinations from the
     * specified start location.
     */
    public String generateTenClosestDestinationsResponseHTML(String start) {
        try {
            String output = "<p> Start Location: " + start + "</p>\n";


            // extracts the ten closest destinations form a giver start point
            List<String> tenClosest = backend.getTenClosestDestinations(start);
            if (!tenClosest.isEmpty()) {


                // creates an ordered list of the ten closest destinations
                output += "<ol>\n";
                for (String i : tenClosest)
                    output += "<li>" + i + "</li>\n";
                output += "</ol>\n";
                return output;
            } else {
                return "<p>No such path exists</p>\n"; //should not be returned ever but acts as a safety measure in case of bad implementation of Backend
            }
        } catch (NoSuchElementException e) {
            return "<p>Either startLocation does not exist, or there are no other locations that can be reached from the start location</p>\n";
        }
    }
}
