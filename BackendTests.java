///////////////////////// TOP OF FILE COMMENT BLOCK ////////////////////////////
//
// Title:           BackendTests
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
import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

/**
 * class that contains three JUnit test mthods that test the functionality and accuracy of Backend.java
 */
public class BackendTests {
    /**
     * The first test case loads the graph data from the capus.dot file that has been provided and retrieves all locations.
     * No exceptions from loadGraphData and getListOfAllLocations returns a non null list.
     */
    @Test
    public void backendTest1() {
        Backend backend = new Backend(new Graph_Placeholder());

        try {
            backend.loadGraphData("campus.dot"); 
        } catch (IOException e) {
            fail("not expected to throw IOException.");
        }

        List<String> locations = backend.getListOfAllLocations();
        assertNotNull(locations);
        assertTrue(locations.contains("Union South"));
        assertTrue(locations.contains("Computer Sciences and Statistics"));
    }
    /**
     * The second test case tests that the backend accurately returns the set of locations along
     * the shortest path between the buildings that are connected in the placeholder graph.
     */
    @Test
    public void backendTest2() {
        Backend backend = new Backend(new Graph_Placeholder());

        List<String> path = backend.findLocationsOnShortestPath(
            "Union South", "Weeks Hall for Geological Sciences");

        assertEquals(List.of(
            "Union South", 
            "Computer Sciences and Statistics", 
            "Weeks Hall for Geological Sciences"
        ), path);

        List<Double> times = backend.findTimesOnShortestPath(
            "Union South", "Weeks Hall for Geological Sciences");
        assertEquals(2, times.size());
        assertEquals(1.0, times.get(0), 0.001);
        assertEquals(2.0, times.get(1), 0.001);
    }
    /**
     * The third test case tests if the backend can identify nearby locations with regard to walking time
     * and includes all of them in a list of closest destinations that will be returned.
     */
    @Test
    public void backendTest3() {
        Backend backend = new Backend(new Graph_Placeholder());

       List<String> closest = backend.getTenClosestDestinations("Union South");
        assertTrue(closest.contains("Computer Sciences and Statistics"));
        
    }

    //////INTEGRATION TEST CASES//////////////////

   /**
   * Integration test 1: This test uses UW–Madison campus locations list through Frontend and
   * loads campus.dot into real Backend then checks that known buildings appear.
   */
  @Test
  public void testIntegrationUWLocations() throws IOException {
      // real graph + backend + frontend
      GraphADT<String,Double> graph = new DijkstraGraph<>();
      Backend backend = new Backend(graph);
      Frontend frontend = new Frontend(backend);

      // load the data
      backend.loadGraphData("campus.dot");

      // get the HTML for “all locations”
      String html = frontend.handleListRequest();

      // assert some UW landmarks are present
      assertTrue(html.contains("Memorial Union"));
      assertTrue(html.contains("Science Hall"));
      assertTrue(html.contains("Union South"));
  }

  /**
   * Integration test 2: This test checks if a simple path is created from alice→bob via real Backend+Frontend.
   */
  @Test
  public void testIntegrationAliceToBob() throws IOException {
      // build a graph with alice→bob
      GraphADT<String,Double> graph = new DijkstraGraph<>();
      graph.insertNode("alice");
      graph.insertNode("bob");
      graph.insertEdge("alice","bob",5.0);

      // Backend + Frontend
      Backend backend = new Backend(graph);
      Frontend frontend = new Frontend(backend);

      // 3) request the path HTML
      String html = frontend.handlePathRequest("alice","bob");

      // cross-check if both names appear
      assertTrue(html.contains("alice"));
      assertTrue(html.contains("bob"));
  }

  /**
   * Integration test 3: this test creates an invalid path between catherine→dia returns an error.
   */
  @Test
  public void testIntegrationCatherineToDiaError() throws IOException {
      // empty graph:
      Backend backend = new Backend(new DijkstraGraph<>());
      Frontend frontend = new Frontend(backend);

      // request a path that cannot exist
      String html = frontend.handlePathRequest("catherine","dia");

      // must show “error” or “invalid”
      String lower = html.toLowerCase();
      assertTrue(lower.contains("error") || lower.contains("invalid"),
                 "Expected an error message for catherine→dia");
  }

  /**
   * Integration test 4: tests for a simple A→B→C end-to-end via real code.
   * Cross-checks if the generic path handling works beyond named nodes.
   */
  @Test
  public void testIntegrationSimpleABCPath() throws IOException {
      // construct A→B→C graph
      GraphADT<String,Double> graph = new DijkstraGraph<>();
      graph.insertNode("A"); graph.insertNode("B"); graph.insertNode("C");
      graph.insertEdge("A","B",1.0);
      graph.insertEdge("B","C",1.0);

      // Backend + Frontend
      Backend backend = new Backend(graph);
      Frontend frontend = new Frontend(backend);

      // request A→C
      String html = frontend.handlePathRequest("A","C");

      // assert A, B, C all present
      assertTrue(html.contains("A"));
      assertTrue(html.contains("B"));
      assertTrue(html.contains("C"));
  }

  /**
   * Integration test 5: this tests ten-closest destinations from “Center” via Backend.
   * Begins by building a star of 12 nodes and verifies only the 10 cheapest show up.
   */
  @Test
  public void testIntegrationTenClosestDestinations() throws IOException {
      //star graph: Center→N1…N12
      GraphADT<String,Double> graph = new DijkstraGraph<>();
      graph.insertNode("Center");
      for (int i = 1; i <= 12; i++) {
          String node = "N" + i;
          graph.insertNode(node);
          graph.insertEdge("Center", node, (double)i);
      }

      // Backend + Frontend
      Backend backend = new Backend(graph);
      Frontend frontend = new Frontend(backend);

      //get the “closest” HTML
      String html = frontend.handleClosestRequest("Center");

      //count list items (<li>) – should be exactly 10
      int count = html.split("<li>").length - 1;
      assertEquals(10, count, "Should list exactly 10 closest destinations");
  }

}
