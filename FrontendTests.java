import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class FrontendTests {
    /**
     * Tests the generateShortestPathResponseHTML() method
     */
    @Test
    public void frontendTest1() {
        Frontend test = new Frontend(new Backend(new DijkstraGraph<>()));
        //normal functioning of method
        assertTrue(test.generateShortestPathResponseHTML("Union South", "Weeks Hall for Geological Sciences").contains("<ol>\n<li>Union South</li>\n<li>Computer Sciences and Statistics</li>\n<li>Weeks Hall for Geological Sciences</li>\n</ol>\n<p> Total Travel Time: 6.0 seconds </p>"));

        //case involving bad user inputs
        assertTrue(test.generateShortestPathResponseHTML("Mosse", "Union South").equals("<p>No such path exists</p>\n"));

        //ensuring that method works even after loadGraphData() method from Frontend Constructor is used
        assertTrue(test.generateShortestPathResponseHTML("Weeks Hall for Geological Sciences", "Mosse Humanities Building").contains("<li>Mosse Humanities Building</li>"));
    }

    /**
     * Tests the generateTenClosestDestinationsResponseHTML() method
     */
    @Test
    public void frontendTest2() {
        Frontend test = new Frontend(new Backend(new DijkstraGraph<>()));

        //checking that the method works even after loadGraphData() method from Frontend Constructor is used
        assertTrue(test.generateTenClosestDestinationsResponseHTML("Union South").contains("<li>Computer Sciences and Statistics</li>\n" +
                "<li>Weeks Hall for Geological Sciences</li>\n" +
                "<li>Mosse Humanities Building</li>"));
    }

    /**
     * Tests the functionality of the prompt methods
     */
    @Test
    public void frontendTest3() {
        //testing generateShortestPathPromptHTML() method
        Frontend test = new Frontend(new Backend(new DijkstraGraph<>()));
        String expected = test.generateShortestPathPromptHTML();
        assertTrue(expected.contains("input type=\"text\" id=\"start\"") && expected.contains("input type=\"text\" id=\"end\"") && expected.contains("input type=\"button\" value=\"Find Shortest Path\"") && expected.split("input").length == 7);

        //testing generateTenClosestDestinationsPromptHTML() method
        String expected2 = test.generateTenClosestDestinationsPromptHTML();
        assertTrue(expected2.contains("input type=\"text\" id=\"from\"") && expected2.contains("input type=\"button\" value=\"Ten Closest Destinations\"") && expected2.split("input").length == 5);
    }

    /**
     * Tests the generateShortestPathResponseHTML() method in a simple manner.
     * <p>
     * Test fails because the backend does not detect an edge from Science Hall to Memorial Union
     * even though there is, and returns an empty ArrayList, causing the frontend to read the total time taken as 0 seconds
     */
    @Test
    public void testIntegration1() {
        Frontend test = new Frontend(new Backend(new DijkstraGraph<>()));
        assertEquals(test.generateShortestPathResponseHTML("Education Building", "Memorial Union"), "<p> Start Location: Education Building; End Location: Memorial Union</p>\n" +
                "<ol>\n" +
                "<li>Education Building</li>\n" +
                "<li>Science Hall</li>\n" +
                "<li>Memorial Union</li>\n" +
                "</ol>\n" +
                "<p> Total Travel Time: 278.8 seconds </p>");
    }

    /**
     * Tests the generateShortestPathResponseHTML() method in a complex manner
     */
    @Test
    public void testIntegration2() {
        //testing destination with a tiebreaker shortest path
        Frontend test = new Frontend(new Backend(new DijkstraGraph<>()));
        assertTrue(test.generateShortestPathResponseHTML("Medical Sciences", "Bradley Memorial Building").contains("<p> Start Location: Medical Sciences; End Location: Bradley Memorial Building</p>\n" +
                "<ol>\n" +
                "<li>Medical Sciences</li>\n") &&
                test.generateShortestPathResponseHTML("Medical Sciences", "Bradley Memorial Building").contains(
                        "<li>Bradley Memorial Building</li>\n" +
                                "</ol>\n" +
                                "<p> Total Travel Time: 153.8 seconds </p>"));
        // testing destinations that are a considerable distance from each other (with many potential locations on the way)
        assertTrue(test.generateShortestPathResponseHTML("Merit Residence Hall","Fleet and Service Garage").equals
                ("<p> Start Location: Merit Residence Hall; End Location: Fleet and Service Garage</p>\n" +
                "<ol>\n" +
                "<li>Merit Residence Hall</li>\n" +
                "<li>Teacher Education</li>\n" +
                "<li>Noland Hall</li>\n" +
                "<li>Meiklejohn House</li>\n" +
                "<li>Computer Sciences and Statistics</li>\n" +
                "<li>Rust-Schreiner Hall</li>\n" +
                "<li>Humbucker Apartments</li>\n" +
                "<li>Harlow Primate Laboratory</li>\n" +
                "<li>Budget Bicycle Center - New Bicycles</li>\n" +
                "<li>Jenson Auto</li>\n" +
                "<li>Phi Kappa Theta</li>\n" +
                "<li>Choles Floral</li>\n" +
                "<li>McDonald's</li>\n" +
                "<li>Fleet and Service Garage</li>\n" +
                "</ol>\n" +
                "<p> Total Travel Time: 1932.4999999999995 seconds </p> \n"));

        // testing whether the method correctly identifies no path
        assertTrue(test.generateShortestPathResponseHTML("Computer Sciences and Statistics","Waters Residence Hall").equals("<p>No such path exists</p>\n"));

    }

    /**
     * Tests generateTenClosestDestinationsResponseHTML() method in a simple manner
     */
    @Test
    public void testIntegration3() {
        // checking whether ten closest destinations are correctly returned
        Frontend test = new Frontend(new Backend(new DijkstraGraph<>()));
        assertTrue(test.generateTenClosestDestinationsResponseHTML("Union South").contains("<li>Wendt Commons</li>\n" +
                "<li>Memorial Arch</li>\n" +
                "<li>1410 Engineering Dr</li>\n" +
                "<li>Computer Sciences and Statistics</li>\n" +
                "<li>Atmospheric, Oceanic and Space Sciences</li>"));
        assertFalse(test.generateTenClosestDestinationsResponseHTML("Union South").contains("<li>Union South<li>"));
    }

    /**
     * Tests the generateTenClosestDestinationsResponseHTML() when the input is invalid
     */
    @Test
    public void testIntegration4() {
        Frontend test = new Frontend(new Backend( new DijkstraGraph<>()));
        assertEquals(test.generateTenClosestDestinationsResponseHTML("Not a Place"),"<p>Either startLocation does not exist, or there are no other locations that can be reached from the start location</p>\n");
    }
}
