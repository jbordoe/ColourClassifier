/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colourclass;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.*;
import javax.imageio.stream.*;

/**
 *
 * @author Jesse Bordoe
 */
public class Img {
    private static final Random rand = new Random();
    private ArrayList<Cluster> clusters;
    private BufferedImage largeImage;
    private BufferedImage scaled;
    private Cluster[][] assignments;
    private String name;
    /**average of all pixels in image */
    private DataPoint average;
    private DataPoint[][] grid;
    /** width of image */
    private int w;
    /** height of image */
    private int h;
    Iterator iter;
    ImageWriter writer;
    ImageWriteParam iwp;
    /** length of longest side in rescaled image */
    private static final int rescale = 200;
    /** number of clusters */
    private static final int k = 6;

    public Img(BufferedImage image, String name) {
        this.name = name;
        this.largeImage = image;
    }

    public void findClusters() {
        int runs = 1;
        scanImage();
        boolean clustersConverged = false;
        clusters = new ArrayList<Cluster>();
        
        /* initialize clusters */
        for (int i = 0; i < k; i++) {
            int centerX = rand.nextInt(w);
            int centerY = rand.nextInt(h);
            DataPoint center = grid[centerX][centerY];
            getClusters().add(new Cluster(center,this));
        }






        /* create initial assignments */
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                assignments[x][y] = null;
            }
        }

        while (!clustersConverged) {
            Cluster[][] newAssignments = new Cluster[w][h];
            System.out.println("run #" + runs);
            HashMap<Cluster, ArrayList<DataPoint>> clusterMapping = new HashMap<Cluster, ArrayList<DataPoint>>();

            for (Cluster c : getClusters()) {
                clusterMapping.put(c, new ArrayList<DataPoint>());
                if (runs > 1) {
                    c.setConverged(true);
                }
            }
            /* map each pixel in image to closest cluster */
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    DataPoint p = grid[x][y];
                    Cluster closest = p.getClosest(getClusters());
                    clusterMapping.get(closest).add(p);
                    newAssignments[x][y] = closest;
                    if (closest != getAssignments()[x][y] && getAssignments()[x][y] != null) {
                        getAssignments()[x][y].setConverged(false);
                        closest.setConverged(false);
                    }
                }
            }
            boolean allConverged = true;
            /* update clusters and check for convergence */
            for (Cluster c : getClusters()) {
                c.setPixels(clusterMapping.get(c));
                allConverged = c.isConverged() && allConverged;
            }
            runs++;
            clustersConverged = allConverged;
            assignments = newAssignments;
        }
        System.out.println("Converged!");
        printClusters();
    }

    private void scanImage() {
        int largeW = largeImage.getWidth();
        int largeH = largeImage.getHeight();
        System.out.println("Original Image: " + largeW + "x" + largeH);
        if (largeW > largeH && largeW > rescale) {
            w = rescale;
            h = (largeH * rescale) / largeW;
        } else if (largeH >= largeW && largeH > rescale) {
            h = rescale;
            w = (largeW * rescale) / largeH;
        } else {
            h = largeH;
            w = largeW;
        }

        scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        AffineTransform at =
                AffineTransform.getScaleInstance(((double) w / largeW), ((double) h / largeH));
        g.drawRenderedImage(largeImage, at);

        System.out.println("Resized to: " + w + "x" + h);
        assignments = new Cluster[w][h];
        grid = new DataPoint[w][h];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = scaled.getRGB(j, i);
                //pixels.add(new DataPoint(pixel,j,i));
                grid[j][i] = new DataPoint(pixel, j, i);
            }
        }
        getAverageColour();
    }

    private void getAverageColour(){
        double avgR = 0.0;
        double avgG = 0.0;
        double avgB = 0.0;
        for(int i = 0; i < w; i++) {
            for(int j = 0; j < h; j++) {
                double[] vals = grid[i][j].getVals();
                avgR += vals[0];
                avgG += vals[1];
                avgB += vals[2];
            }
        }
        avgR = avgR/(w*h);
        avgG = avgG/(w*h);
        avgB = avgB/(w*h);
        average = new DataPoint(new int[]{(int)avgR,(int)avgG,(int)avgB},0,0);
    }

    private void printClusters() {
        System.out.println("\tCluster Centers:");
        for (Cluster c : getClusters()) {
            System.out.println("\t"+c.getCenter().toHexTriplet());
        }
    }


    public String outputClusteringResults() {
        DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
        DecimalFormat threeDigit = new DecimalFormat("#,##0.000");
        sortClustersBySize();
        int n = 0;

        StringBuffer results = new StringBuffer();

        /* set up jpeg output params */
        iter = ImageIO.getImageWritersByFormatName("jpeg");
        writer = (ImageWriter)iter.next();
        // instantiate an ImageWriteParam object with default compression options
        iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(1);
        outputJPEG(scaled, "C:/Temp/cluster/thumbs/"+name+".jpg");
        BufferedImage segmentedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        results.append("<div>");
        results.append("<img src=\"thumbs/"+name+".jpg\" id=\"pic-"+name+"\">" +
                "<img src=\"thumbs/"+name+"-seg.jpg\" id=\"seg-"+name+"\">"+
                "<img src=\"thumbs/"+name+".jpg\" id=\"cut-"+name+"\">" +
                "<img src=\"thumbs/"+name+".jpg\" id=\"avg-"+name+"\"><br>");
        for (Cluster c : getClusters()) {
            if(c.getSize()==0){
                continue;
            }
            
            //int percentage = (c.getSize()*100)/(w*h);
            String percentage = oneDigit.format(((double)c.getSize()*100)/(w*h));
            results.append("<div class=\"palette\" style=\"" +
                    "background-color: " + c.getCenter().toHexTriplet() + "\" " +
                    "onmouseover=\"document.getElementById('cut-"+name+"').src='thumbs/"+name+""+n+".jpg'; " +
                    "document.getElementById('avg-"+name+"').src='thumbs/"+name+""+n+"-avg.jpg';\" " +
                    "onmouseout=\"document.getElementById('avg-"+name+"').src='thumbs/"+name+".jpg'; " +
                    "document.getElementById('cut-"+name+"').src='thumbs/"+name+".jpg';\" >" + c.getCenter().toHexTriplet() + "" +
                    "<div class=\"size\">"+percentage+"%</div></div>");

            /* create graphical representation of cluster */
            ArrayList<DataPoint> members = c.getPixels();
            BufferedImage clusterImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            BufferedImage averagedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            
            for(DataPoint p: members){
                int pX = (int)p.getX();
                int pY = (int)p.getY();
                clusterImg.setRGB(pX,pY,p.getArgb());
                averagedImg.setRGB(pX,pY,12135352);
                segmentedImg.setRGB(pX,pY,c.getCenter().getArgb());
            }            
            outputJPEG(clusterImg,"C:/Temp/cluster/thumbs/"+name+""+n+".jpg");
            outputJPEG(averagedImg,"C:/Temp/cluster/thumbs/"+name+""+n+"-avg.jpg");
            n++;
        }
        outputJPEG(segmentedImg,"C:/Temp/cluster/thumbs/"+name+"-seg.jpg");

        writer.dispose();
        results.append("<br><table class=\"stats\"><tr><td><b>Stats</b></td></tr>" +
                "<tr><td>Average colour: "+average.toHexTriplet());
        results.append("<div class = \"average\" " +
        "style=\"background-color:"+average.toHexTriplet()+"\"></div></td></tr>" +
                "<tr><td>Sorted By Rank:<br>");

        ArrayList<Cluster> ranked = sortClustersByRank();
        for (Cluster c : ranked) {
            if(c.getSize()==0){
                continue;
            }
            results.append("<div class = \"small-palette\" " +
                 "style=\"background-color:"+c.getCenter().toHexTriplet()+"\">" +
                 threeDigit.format(c.getRank())+"</div>");
        }
        String colour = ranked.get(0).getCenter().closestColour();
        results.append("</td></tr><tr><td><div id=\""+colour+"\" class=\"average\"></div>Classified as "+colour+"</td></tr></table>");
        results.append("</div>");
        //results.append("</td></tr></table></body></html>");
        BufferedWriter out = null;
//        try {
//            FileOutputStream fstream = new FileOutputStream("C:/Temp/cluster/results.html");
//            out = new BufferedWriter(new OutputStreamWriter(fstream, "cp1250"));
//            out.write(html);
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//        } finally { /* Ensure file is closed no matter what */
//            if (out != null) {
//                try {
//                    out.close(); /* Close output stream */
//                } catch (Exception e) {
//                    System.err.println("Error: " + e.getMessage());
//                }
//            }
//        }
        return results.toString();
    }

    private void outputJPEG(BufferedImage img, String filename){
        try {
            FileImageOutputStream output = new FileImageOutputStream(new File(filename));
            writer.setOutput(output);
            IIOImage image = new IIOImage(img, null, null);
            writer.write(null, image, iwp);
        } catch (Exception e) {
            //
        }
    }

    /**
     * put clusters in order of size (insertion sort)
     */
    private void sortClustersBySize(){
        ArrayList<Cluster> sorted = new ArrayList<Cluster>();
        while (getClusters().size() > 0) {
            boolean inserted = false;
            Cluster c = getClusters().remove(0);
            for(int i = 0; i<sorted.size(); i++) {
                if(sorted.get(i).getSize() < c.getSize()){
                    sorted.add(i,c);
                    inserted = true;
                    break;
                }                
            }
            if(!inserted){
                sorted.add(c); //..otherwise add to end of list
            }
        }
        clusters = sorted;
    }

    /**
     * put clusters in order of rank (insertion sort)
     */
    private ArrayList<Cluster> sortClustersByRank(){
        for(Cluster c: getClusters()){
            c.rank(w,h);
        }
        ArrayList<Cluster> sorted = new ArrayList<Cluster>();
        while (getClusters().size() > 0) {
            boolean inserted = false;
            Cluster c = getClusters().remove(0);
            for(int i = 0; i<sorted.size(); i++) {
                if(sorted.get(i).getRank() < c.getRank()){
                    sorted.add(i,c);
                    inserted = true;
                    break;
                }
            }
            if(!inserted){
                sorted.add(c); //..otherwise add to end of list
            }
        }
        return sorted;
    }



    private void threshold() {
        for(int i = 0; i<w; i++){
            for(int j = 0; j<h; j++){
                grid[i][j].RGBThreshold(1);
                scaled.setRGB(i,j,grid[i][j].getArgb());
            }
        }
    }

    /**
     * @return the clusters
     */
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    /**
     * @return the assignments
     */
    public Cluster[][] getAssignments() {
        return assignments;
    }
}
