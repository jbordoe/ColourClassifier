/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package colourclass;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a pixel in the image
 * @author Jesse Bordoe
 */
public class DataPoint {
    /* dimensions in (a)RGB colour space */
    private int r;
    private int g;
    private int b;
    private int x_pos;
    private int y_pos;
    /* dimensions for CIE 1976(L*,a*,b*) colour space */
    /** luminosity */
    protected double CIE_l;
    /** red-green axis */
    protected double CIE_a;
    /** blue-yellow axis */
    protected double CIE_b;
    /** the cluster this point is assigned to */
    private Cluster cluster;
    /** full aRGB bitstring */
    private int argb;
    /** saturation */
    private double sat;

    public DataPoint(){

    }
    
    public DataPoint(int[] rgb, int x, int y){
        this.r = rgb[0];
        this.g = rgb[1];
        this.b = rgb[2];
        this.x_pos = x;
        this.y_pos = y;
        this.argb = (0xff << 24) | ((int)rgb[0] << 16) | ((int)rgb[1] << 8) | (int)rgb[2];
        sat = Math.max(Math.max(r,g),b) - Math.min(Math.min(r,g),b);
        double[]cie = Converter.RGBtoCIE(r,g,b);
        CIE_l = cie[0];
        CIE_a = cie[1];
        CIE_b = cie[2];
    }

    public DataPoint(int pixel,int x, int y){
        //int alpha = (pixel >> 24) & 0xff;
        this.argb = pixel;
        this.x_pos = x;
        this.y_pos = y;
        int ri = (pixel >> 16) & 0xff;
        int gi = (pixel >> 8) & 0xff;
        int bi = (pixel) & 0xff;
        r = ri;
        g = gi;
        b = bi;
        sat = Math.max(Math.max(r,g),b) - Math.min(Math.min(r,g),b);
        double[]cie = Converter.RGBtoCIE(r,g,b);
        CIE_l = cie[0];
        CIE_a = cie[1];
        CIE_b = cie[2];
    }
    /**
     * @return String representation of RBG colour for use in CSS/HTML
     */
    public String toHexTriplet(){
        double[] rgb = {r,g,b};        
        String triplet = "#";
        for (int i=0; i<3; i++){
            int rounded = (int)Math.round(rgb[i]);
            if(rounded > 255){ rounded = 255;}
            else if(rounded < 0){ rounded = 0;}
            String col = java.lang.Integer.toHexString(rounded);
            if (col.length()==1){
                col = "0"+col;
            }
            triplet = triplet+col;
        }
        return triplet;
    }

    /**
     * @param p point to calculate distance from
     * @return euclidian distance of this point and argument point
     */
    private double distanceFrom(DataPoint p){
        //double[] vals = p.getVals();
        double[] cie = p.getCIE();
        //return Math.sqrt(Math.pow((r-vals[0])/255,2) + Math.pow((g-vals[1])/255,2) + Math.pow((b-vals[2])/255,2));
        return Math.sqrt(Math.pow(cie[0]-CIE_l, 2)*0.5 //CIE luminosity
                + Math.pow(cie[1]-CIE_a, 2)         //CIE red-green axis
                + Math.pow(cie[2]-CIE_b, 2)         //CIE blue-yellow axis
                );
    }

        /**
     * @param p point to calculate distance from
     * @return euclidian distance of this point and argument point
     */
    private double trueDistanceFrom(DataPoint p){
        //double[] vals = p.getVals();
        double[] cie = p.getCIE();
        //return Math.sqrt(Math.pow((r-vals[0])/255,2) + Math.pow((g-vals[1])/255,2) + Math.pow((b-vals[2])/255,2));
        return Math.sqrt(Math.pow(cie[0]-CIE_l, 2) //CIE luminosity
                + Math.pow(cie[1]-CIE_a, 2)         //CIE red-green axis
                + Math.pow(cie[2]-CIE_b, 2)         //CIE blue-yellow axis
                );
    }
    /**
     *
     * @return arroy containing values of this data point, in order r,g,b,saturation,x,y
     */
    public double[] getVals() {
        return new double[]{r, g, b, sat, x_pos, y_pos};
    }

    public Cluster getClosest(ArrayList<Cluster> clusters) {
        Cluster closestCluster = null;
        double closestDistance = Double.MAX_VALUE;
        for (Cluster c : clusters) {
            double dist = distanceFrom(c.getCenter());
            if (closestCluster == null || dist < closestDistance) {
                closestCluster = c;
                closestDistance = dist;
            }
        }
        return closestCluster;
    }

    /**
     * @return the ARGB value of the pixel
     */
    public int getArgb() {
        argb = (0xff << 24) | ((int)r << 16) | ((int)g << 8) | (int)b;
        return argb;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x_pos;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y_pos;
    }
    
   
     public void CIEtoRGB(){
        double[] rgb = Converter.CIEtoRGB(CIE_l,CIE_a,CIE_b);
        r = (int)rgb[0];
        g = (int)rgb[1];
        b = (int)rgb[2];
    }

     public double getSaturation(){
         int min = Math.min(r, Math.min(g,b));
         int max = Math.max(r, Math.max(g,b));
         if(max == 0 || max == min){
             return 0.0;
         }
         return (((double)(max-min))/255);
     }

     public String classify(){
         DataPoint red = new DataPoint(new int[]{255,0,0},0,0);
         DataPoint blue = new DataPoint(new int[]{0,0,255},0,0);
         DataPoint teal = new DataPoint(new int[]{0,127,255},0,0);
         DataPoint purple = new DataPoint(new int[]{127,0,255},0,0);
         DataPoint black = new DataPoint(new int[]{0,0,0},0,0);
         return "";
     }

     public void RGBThreshold(int levels){
         r = (int) (Math.round((double)r*levels/255) * ((double)255/levels));
         g = (int) (Math.round((double)g*levels/255) * ((double)255/levels));
         b = (int) (Math.round((double)b*levels/255) * ((double)255/levels));
     }

     public String closestColour(){
         double closest = Double.MAX_VALUE;
         String colour = "";
         HashMap<DataPoint,String> colours = new HashMap<DataPoint,String>();
         colours.put(new DataPoint(0xfff00000,0,0),"red");
         colours.put(new DataPoint(0xffff8000,0,0),"orange");
         colours.put(new DataPoint(0xffffff00,0,0),"yellow");
         colours.put(new DataPoint(0xff00ff00,0,0),"green");
         colours.put(new DataPoint(0xff00d8ff,0,0),"teal");
         colours.put(new DataPoint(0xff0000f0,0,0),"blue");
         colours.put(new DataPoint(0xff7f00ff,0,0),"purple");
         colours.put(new DataPoint(0xff000000,0,0),"black");
         colours.put(new DataPoint(0xffffffff,0,0),"white");
         colours.put(new DataPoint(0xff808080,0,0),"grey");
         colours.put(new DataPoint(0xffff7Fb6,0,0),"pink");
         colours.put(new DataPoint(0xff723d19,0,0),"brown");
         colours.put(new DataPoint(0xfffbc8ab,0,0),"flesh");

         for(DataPoint d: colours.keySet()){
             double dist  = trueDistanceFrom(d);
             if(dist<closest){
                 colour = colours.get(d);
                 closest = dist;
             }
         }

         return colour;
     }






    public double[] getCIE(){
        return new double[]{CIE_l,CIE_a,CIE_b};
    }




}
