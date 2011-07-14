/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package colourclass;

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
public class CenterTest {

    public CenterTest() {
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
     * Test of CIEtoRGB method, of class Center.
     */
    @Test
    public void testCIEtoRGB() {
        System.out.println("CIEtoRGB");
        Center instance = null;
        instance.CIEtoRGB();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toHexTriplet method, of class Center.
     */
    @Test
    public void testToHexTriplet() {
        System.out.println("toHexTriplet");
        Center instance = new Center(new int[]{255,255,255},0,0);
        String expResult = "#ffffff";
        String result = instance.toHexTriplet();
        assertEquals(expResult, result);

        instance = new Center(new int[]{0,1,16},0,0);
        expResult = "#000110";
        result = instance.toHexTriplet();
        assertEquals(expResult, result);
    }

    /**
     * Test of getArgb method, of class Center.
     */
    @Test
    public void testGetArgb() {
        System.out.println("getArgb");
        Center instance = null;
        int expResult = 0;
        int result = instance.getArgb();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}