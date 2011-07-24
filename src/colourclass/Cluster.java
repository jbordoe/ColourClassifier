/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package colourclass;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Jesse Bordoe
 */
public class Cluster {
    private static final Random rand = new Random();
    private DataPoint center;
    /** pixels assigned to this cluster */
    private ArrayList<DataPoint> pixels;
    private boolean converged = false;
    private double rank;
    private Img parent;

    public Cluster(DataPoint d, Img parent){
        center = d;
        pixels = new ArrayList<DataPoint>();
        this.parent = parent;
    }

    /**
     * @return the center point of this cluster
     */
    public DataPoint getCenter() {
        return center;
    }

    /**
     * @param pixels the pixels to set
     */
    public void setPixels(ArrayList<DataPoint> pixels) {
        //System.out.print("P ");
        //setConverged(Tools.same(pixels, this.pixels));
        //System.out.println("C");
        this.pixels = pixels;
        if(!converged){
            reset();
        } else {
            //System.out.println("converged!");
        }
    }

    /**
     * reposition this clusters center to mean of all member pixels
     */
    private void reset(){
        double meanR = 0.0;
        double meanG = 0.0;
        double meanB = 0.0;
        double meanX = 0.0;
        double meanY = 0.0;
        double meanCIE_l = 0.0;
        double meanCIE_a = 0.0;
        double meanCIE_b = 0.0;
        
        int members = pixels.size();
        for(int i=0; i<members; i++){
            double[] tempRBG = pixels.get(i).getVals();
            meanR += tempRBG[0];
            meanG += tempRBG[1];
            meanB += tempRBG[2];
            meanX += tempRBG[4];
            meanY += tempRBG[5];
            double[] tempCIE = pixels.get(i).getCIE();
            meanCIE_l += tempCIE[0];
            meanCIE_a += tempCIE[1];
            meanCIE_b += tempCIE[2];
        }
        meanR = meanR/members;
        meanG = meanG/members;
        meanB = meanB/members;
        meanX = meanX/members;   
        meanY = meanY/members;

        meanCIE_l = meanCIE_l/members;
        meanCIE_a = meanCIE_a/members;
        meanCIE_b = meanCIE_b/members;
        center = new Center("CIE",new double[]{meanCIE_l,meanCIE_a,meanCIE_b},meanX,meanY);
        //center = new DataPoint(new double[]{meanR,meanG,meanB},meanX,meanY);
        //System.out.println(center.toHexTriplet());
    }

    /**
     * @return the converged
     */
    public boolean isConverged() {
        return converged;
    }

    /**
     * @param converged the converged to set
     */
    public void setConverged(boolean converged) {
        this.converged = converged;
    }
    /**
     * @return number of pixels in this cluster
     */
    public int getSize(){
        return pixels.size();
    }

    /**
     * @return the pixels
     */
    public ArrayList<DataPoint> getPixels() {
        return pixels;
    }

    public void rank(int w, int h){
        double centerX = (double)w/2;
        double centerY = (double)h/2;
        double centered = 0.0;
        double sat = 0.0;
        double percentage = (double)pixels.size()/(w*h);
        double maxDist = Math.sqrt(Math.pow(centerX,2) + Math.pow(centerY,2));
        for(DataPoint d: pixels){
            centered += 1 - Math.sqrt(Math.pow(d.getX()-centerX,2) + Math.pow(d.getY()-centerY,2))/maxDist;
            sat += d.getSaturation();
        }
        centered = centered/pixels.size();
        sat = sat / pixels.size();
        rank = Math.pow(percentage,0.5) * (centered*0.5 + sat*0.4 + getSpread()*0.3);
    }

    /**
     * @return the rank
     */
    public double getRank() {
        return rank;
    }

    private double getSpread(){
        Cluster[][] assignments = parent.getAssignments();
        double score = 0;
        for(DataPoint d: pixels){
            int x = d.getX();
            int y = d.getY();
            int compared = 0;
            int same = 0;
            //check the 3x3 grid round the pixel
            for(int i = x-1; i < x+2; i++){
                if(i < 0 || i >= assignments.length){
                    continue;
                }
                for(int j = y-1; j < y+2; j++){
                    if(j<0 || j>= assignments[0].length ){
                        continue;
                    }
                    compared++;
                    if(assignments[i][j]==this){
                        same++;
                    }
                }
            }
            score += (double)same/compared;
        }
        return score/pixels.size();
    }

}
