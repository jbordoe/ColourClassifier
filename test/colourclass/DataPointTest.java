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
public class DataPointTest {

    public DataPointTest() {
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
     * Test of toHexTriplet method, of class DataPoint.
     */
    @Test
    public void testToHexTriplet() {
        System.out.println("toHexTriplet");
        DataPoint instance = new DataPoint(new int[]{255,255,255},0,0);
        String expResult = "#ffffff";
        String result = instance.toHexTriplet();
        assertEquals(expResult, result);

        instance = new DataPoint(new int[]{0,1,16},0,0);
        expResult = "#000110";
        result = instance.toHexTriplet();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVals method, of class DataPoint.
     */
    @Test
    public void testGetVals() {
        System.out.println("getVals");
        DataPoint instance = null;
        double[] expResult = null;
        double[] result = instance.getVals();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClosest method, of class DataPoint.
     */
    @Test
    public void testGetClosest() {
        System.out.println("getClosest");
        ArrayList<Cluster> clusters = null;
        DataPoint instance = null;
        Cluster expResult = null;
        Cluster result = instance.getClosest(clusters);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArgb method, of class DataPoint.
     */
    @Test
    public void testGetArgb() {
        System.out.println("getArgb");
        DataPoint instance = new DataPoint(0xfff0f0f0,0,0);
        int expResult = 0xfff0f0f0;
        int result = instance.getArgb();
        assertEquals(expResult, result);
    }

    /**
     * Test of getX method, of class DataPoint.
     */
    @Test
    public void testGetX() {
        System.out.println("getX");
        DataPoint instance = null;
        double expResult = 0.0;
        double result = instance.getX();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getY method, of class DataPoint.
     */
    @Test
    public void testGetY() {
        System.out.println("getY");
        DataPoint instance = null;
        double expResult = 0.0;
        double result = instance.getY();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }




}