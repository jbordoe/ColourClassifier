package colourclass;

import Clusterer.Clusterer;
import Clusterer.Cluster;
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
    private BufferedImage largeImage;
    private BufferedImage scaled;
    private ImgCluster[][] assignments;
    private String name;
    /**
     * average of all pixels in image
     */
    private DataPoint average;
    private DataPoint[][] grid;
    /**
     * width of image
     */
    private int width;
    /**
     * height of image
     */
    private int height;
    
    Iterator iter;
    ImageWriter writer;
    ImageWriteParam iwp;
    /**
     * length of longest side in rescaled image, in pixels
     */
    private static final int rescale = 200;
    /**
     * number of clusters
     */
    private static final int k = 6;
    /**
     * Weight for x & y coordinates in transformed dataset for clustering Keep
     * this low if clusters should ignore local colour similarity High if
     * clusters are intended to represent spatial data too
     */
    private static final double spatialWeight = 0.01;

    public Img(BufferedImage image, String name) {
        this.name = name;
        this.largeImage = image;
        this.height = image.getHeight();
        this.width = image.getWidth();
    }

    public void findClusters() {
        double[][] dataset = to2DArray();

        Clusterer clusterer = new Clusterer(dataset);
        clusterer.setVerbose(true);
        clusterer.setK(k);

        Cluster[] clusters = clusterer.findClusters();
        outputClusteredImage(clusters);


    }

    public void outputClusteredImage(Cluster[] clusters) {
        iter = ImageIO.getImageWritersByFormatName("jpeg");
        writer = (ImageWriter) iter.next();
        // instantiate an ImageWriteParam object with default compression options
        iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(1);
        BufferedImage clusteredImg = new BufferedImage(largeImage.getWidth(), largeImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (Cluster cluster : clusters) {
            double[][] dataPoints = cluster.getDataPoints();
            double[] center = cluster.getCenter();
            double cie1 = center[2];
            double cie2 = center[3];
            double cie3 = center[4];
            double[] rgb = ColourspaceConverter.CIEtoRGB(cie1, cie2, cie3);
            int rgbint = ((int) rgb[0] << 16) | ((int) rgb[1] << 8) | ((int) rgb[2]);
            for (double[] d : dataPoints) {
                int x = (int) (d[0] / spatialWeight);
                int y = (int) (d[1] / spatialWeight);

                clusteredImg.setRGB(x, y, rgbint);
            }
        }
        outputJPEG(clusteredImg, "C:/Temp/cluster/test/" + System.currentTimeMillis() + ".jpg");
    }

    public double[][] to2DArray() {
        System.out.println("W: " + width + ", H: " + height);

        double[][] dataset = new double[width * height][5];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = largeImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb) & 0xff;
                double[] cie = ColourspaceConverter.RGBtoCIE(r, g, b);

                double tx = ((double) x) * spatialWeight;
                double ty = ((double) y) * spatialWeight;

                dataset[x + y * width] = new double[]{tx, ty, cie[0], cie[1], cie[2]};
            }
        }
        return dataset;
    }
    
    private void scanImage() {
        int largeW = largeImage.getWidth();
        int largeH = largeImage.getHeight();
        System.out.println("Original Image: " + largeW + "x" + largeH);
        if (largeW > largeH && largeW > rescale) {
            width = rescale;
            height = (largeH * rescale) / largeW;
        } else if (largeH >= largeW && largeH > rescale) {
            height = rescale;
            width = (largeW * rescale) / largeH;
        } else {
            height = largeH;
            width = largeW;
        }

        scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        AffineTransform at =
                AffineTransform.getScaleInstance(((double) width / largeW), ((double) height / largeH));
        g.drawRenderedImage(largeImage, at);

        System.out.println("Resized to: " + width + "x" + height);
        assignments = new ImgCluster[width][height];
        grid = new DataPoint[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = scaled.getRGB(j, i);
                //pixels.add(new DataPoint(pixel,j,i));
                grid[j][i] = new DataPoint(pixel, j, i);
            }
        }
        getAverageColour();
    }

    private void getAverageColour() {
        double avgR = 0.0;
        double avgG = 0.0;
        double avgB = 0.0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double[] vals = grid[i][j].getVals();
                avgR += vals[0];
                avgG += vals[1];
                avgB += vals[2];
            }
        }
        avgR = avgR / (width * height);
        avgG = avgG / (width * height);
        avgB = avgB / (width * height);
        average = new DataPoint(new int[]{(int) avgR, (int) avgG, (int) avgB}, 0, 0);
    }

    private void printClusters() {
        System.out.println("\tCluster Centers:");
        for (ImgCluster c : getClusters()) {
            System.out.println("\t" + c.getCenter().toHexTriplet());
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
        writer = (ImageWriter) iter.next();
        // instantiate an ImageWriteParam object with default compression options
        iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(1);
        outputJPEG(scaled, "C:/Temp/cluster/thumbs/" + name + ".jpg");
        BufferedImage segmentedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        results.append("<div>");
        results.append("<img src=\"thumbs/" + name + ".jpg\" id=\"pic-" + name + "\">"
                + "<img src=\"thumbs/" + name + "-seg.jpg\" id=\"seg-" + name + "\">"
                + "<img src=\"thumbs/" + name + ".jpg\" id=\"cut-" + name + "\">"
                + "<img src=\"thumbs/" + name + ".jpg\" id=\"avg-" + name + "\"><br>");
        for (ImgCluster c : getClusters()) {
            if (c.getSize() == 0) {
                continue;
            }

            //int percentage = (c.getSize()*100)/(width*height);
            String percentage = oneDigit.format(((double) c.getSize() * 100) / (width * height));
            results.append("<div class=\"palette\" style=\""
                    + "background-color: " + c.getCenter().toHexTriplet() + "\" "
                    + "onmouseover=\"document.getElementById('cut-" + name + "').src='thumbs/" + name + "" + n + ".jpg'; "
                    + "document.getElementById('avg-" + name + "').src='thumbs/" + name + "" + n + "-avg.jpg';\" "
                    + "onmouseout=\"document.getElementById('avg-" + name + "').src='thumbs/" + name + ".jpg'; "
                    + "document.getElementById('cut-" + name + "').src='thumbs/" + name + ".jpg';\" >" + c.getCenter().toHexTriplet() + ""
                    + "<div class=\"size\">" + percentage + "%</div></div>");

            /* create graphical representation of cluster */
            ArrayList<DataPoint> members = c.getPixels();
            BufferedImage clusterImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            BufferedImage averagedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (DataPoint p : members) {
                int pX = (int) p.getX();
                int pY = (int) p.getY();
                clusterImg.setRGB(pX, pY, p.getArgb());
                averagedImg.setRGB(pX, pY, 12135352);
                segmentedImg.setRGB(pX, pY, c.getCenter().getArgb());
            }
            outputJPEG(clusterImg, "C:/Temp/cluster/thumbs/" + name + "" + n + ".jpg");
            outputJPEG(averagedImg, "C:/Temp/cluster/thumbs/" + name + "" + n + "-avg.jpg");
            n++;
        }
        outputJPEG(segmentedImg, "C:/Temp/cluster/thumbs/" + name + "-seg.jpg");

        writer.dispose();
        results.append("<br><table class=\"stats\"><tr><td><b>Stats</b></td></tr>"
                + "<tr><td>Average colour: " + average.toHexTriplet());
        results.append("<div class = \"average\" "
                + "style=\"background-color:" + average.toHexTriplet() + "\"></div></td></tr>"
                + "<tr><td>Sorted By Rank:<br>");

        ArrayList<ImgCluster> ranked = sortClustersByRank();
        for (ImgCluster c : ranked) {
            if (c.getSize() == 0) {
                continue;
            }
            results.append("<div class = \"small-palette\" "
                    + "style=\"background-color:" + c.getCenter().toHexTriplet() + "\">"
                    + threeDigit.format(c.getRank()) + "</div>");
        }
        String colour = ranked.get(0).getCenter().closestColour();
        results.append("</td></tr><tr><td><div id=\"" + colour + "\" class=\"average\"></div>Classified as " + colour + "</td></tr></table>");
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

    private void outputJPEG(BufferedImage img, String filename) {
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
    private void sortClustersBySize() {
        ArrayList<ImgCluster> sorted = new ArrayList<ImgCluster>();
        while (getClusters().size() > 0) {
            boolean inserted = false;
            ImgCluster c = getClusters().remove(0);
            for (int i = 0; i < sorted.size(); i++) {
                if (sorted.get(i).getSize() < c.getSize()) {
                    sorted.add(i, c);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                sorted.add(c); //..otherwise add to end of list
            }
        }
        //clusters = sorted;
    }

    /**
     * put clusters in order of rank (insertion sort)
     */
    private ArrayList<ImgCluster> sortClustersByRank() {
        for (ImgCluster c : getClusters()) {
            c.rank(width, height);
        }
        ArrayList<ImgCluster> sorted = new ArrayList<ImgCluster>();
        while (getClusters().size() > 0) {
            boolean inserted = false;
            ImgCluster c = getClusters().remove(0);
            for (int i = 0; i < sorted.size(); i++) {
                if (sorted.get(i).getRank() < c.getRank()) {
                    sorted.add(i, c);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                sorted.add(c); //..otherwise add to end of list
            }
        }
        return sorted;
    }

    private void threshold() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j].RGBThreshold(1);
                scaled.setRGB(i, j, grid[i][j].getArgb());
            }
        }
    }

    /**
     * @return the clusters
     */
    public ArrayList<ImgCluster> getClusters() {
        return null; //clusters;
    }

    /**
     * @return the assignments
     */
    public ImgCluster[][] getAssignments() {
        return assignments;
    }
}
