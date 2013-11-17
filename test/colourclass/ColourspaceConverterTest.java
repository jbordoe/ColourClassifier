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
public class ColourspaceConverterTest {
    /** epsilon */
    final double e = 0.01;
    public ColourspaceConverterTest() {
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
     * Test of RGBtoCIE method, of class ColourspaceConverter.
     */
    @Test
    public void testRGBtoCIE() {
        System.out.println("RGBtoCIE");
        double r = 255.0;
        double g = 171.0;
        double b = 32.0;
        double[] result = ColourspaceConverter.RGBtoCIE(r, g, b);
        assertEquals(76.369, result[0],e);
        assertEquals(21.182, result[1],e);
        assertEquals(74.945, result[2],e);
        result = ColourspaceConverter.RGBtoCIE(0.0, 0.0, 0.0);
        assertEquals(0.0, result[0],e);
        assertEquals(0.0, result[1],e);
        assertEquals(0.0, result[2],e);
    }

    /**
     * Test of CIEtoRGB method, of class ColourspaceConverter.
     */
    @Test
    public void testCIEtoRGB() {
        System.out.println("CIEtoRGB");
        double l = 76.369;
        double a = 21.182;
        double b = 74.945;
        double[] result = ColourspaceConverter.CIEtoRGB(l, a, b);
        assertEquals(255.0, result[0],e);
        assertEquals(171.0, result[1],e);
        assertEquals(32.0, result[2],e);
        result = ColourspaceConverter.CIEtoRGB(0.0, 0.0, 0.0);
        assertEquals(0.0, result[0],e);
        assertEquals(0.0, result[1],e);
        assertEquals(0.0, result[2],e);
    }

    /**
     * Test of RGBtoXYZ method, of class ColourspaceConverter.
     */
    @Test
    public void testRGBtoXYZ() {
        System.out.println("RGBtoXYZ");
        double r = 255.0;
        double g = 171.0;
        double b = 32.0;
        double[] result = ColourspaceConverter.RGBtoXYZ(r, g, b);
        System.out.print("X");
        assertEquals(56.064, result[0],e);
        System.out.print(", Y");
        assertEquals(50.490, result[1],e);
        System.out.print(", Z\n");
        assertEquals(8.157, result[2],e);
    }

    /**
     * Test of XYZtoCIE method, of class ColourspaceConverter.
     */
    @Test
    public void testXYZtoCIE() {
        System.out.println("XYZtoCIE");
        double[] xyz = {56.064,50.490,8.157};
        double[] result = ColourspaceConverter.XYZtoCIE(xyz);
        System.out.print("C");
        assertEquals(76.369, result[0],e);
        System.out.print(", I");
        assertEquals(21.182, result[1],e);
        System.out.print(", E\n");
        assertEquals(74.945, result[2],e);

    }

    /**
     * Test of CIEtoXYZ method, of class ColourspaceConverter.
     */
    @Test
    public void testCIEtoXYZ() {
        System.out.println("CIEtoXYZ");
        double CIE_l = 76.369;
        double CIE_a = 21.182;
        double CIE_b = 74.945;
        double[] result = ColourspaceConverter.CIEtoXYZ(CIE_l, CIE_a, CIE_b);
        assertEquals(56.064, result[0],e);
        assertEquals(50.490, result[1],e);
        assertEquals(8.157, result[2],e);
        result = ColourspaceConverter.CIEtoXYZ(0.0, 0.0, 0.0);
        assertEquals(0.0, result[0],e);
        assertEquals(0.0, result[1],e);
        assertEquals(0.0, result[2],e);
    }

    /**
     * Test of XYZtoRGB method, of class ColourspaceConverter.
     */
    @Test
    public void testXYZtoRGB() {
        System.out.println("XYZtoRGB");
        double[] xyz = {56.064,50.490,8.157};
        double[] result = ColourspaceConverter.XYZtoRGB(xyz);
        assertEquals(255.0, result[0],e);
        assertEquals(171.0, result[1],e);
        assertEquals(32.0, result[2],e);
        result = ColourspaceConverter.XYZtoRGB(new double[]{0.0,0.0,0.0});
        assertEquals(0.0, result[0],e);
        assertEquals(0.0, result[1],e);
        assertEquals(0.0, result[2],e);

    }

}