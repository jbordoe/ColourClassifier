/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package colourclass;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jesse-SouljaBoy-FanB
 */
public class ClusterTest {

    public ClusterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getCenter method, of class Cluster.
     */
    @Test
    public void testGetCenter() {
        System.out.println("getCenter");
        Cluster instance = null;
        DataPoint expResult = null;
        DataPoint result = instance.getCenter();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPixels method, of class Cluster.
     */
    @Test
    public void testSetPixels() {
        System.out.println("setPixels");
        ArrayList<DataPoint> pixels = null;
        Cluster instance = null;
        instance.setPixels(pixels);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isConverged method, of class Cluster.
     */
    @Test
    public void testIsConverged() {
        System.out.println("isConverged");
        Cluster instance = null;
        boolean expResult = false;
        boolean result = instance.isConverged();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setConverged method, of class Cluster.
     */
    @Test
    public void testSetConverged() {
        System.out.println("setConverged");
        boolean converged = false;
        Cluster instance = null;
        instance.setConverged(converged);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSize method, of class Cluster.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        Cluster instance = null;
        int expResult = 0;
        int result = instance.getSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPixels method, of class Cluster.
     */
    @Test
    public void testGetPixels() {
        System.out.println("getPixels");
        Cluster instance = null;
        ArrayList<DataPoint> expResult = null;
        ArrayList<DataPoint> result = instance.getPixels();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}