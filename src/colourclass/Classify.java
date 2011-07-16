package colourclass;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.*;
/**
 *
 * @author Jesse Bordoe
 */
public class Classify {

    public static String imgPath;
    public static BufferedImage image;
    /** number of clustering results to show per page */
    private static final int perPage = 3;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* get image path from command line args */
        try {
            String path = args[0];
            if(path.equals("-help") || path.equals("-h")){
                showHelp();
                return;
            }
            if(path.matches(".*?\\.(jpg|gif|jpeg)")){
                init();
                System.out.println("reading single image");
                String res = clusterImage(path);
                outputResults(new String[]{res});
            } else {
                init();
                System.out.println("reading folder");
                String[] results = clusterFromFolder(path);
                outputResults(results);
            }



//            File dir = new File("C:/wamp/www/Pitchfork/Thumbs/Large");
//            File[] files = dir.listFiles();
//            Random r = new Random();
//            int n = r.nextInt(files.length);
//            imgPath = args[0];
//            System.out.println("opening file: "+imgPath);
//            //File imgFile = new File(imgPath);
//            File imgFile = files[n];
//            if(!imgFile.isFile()) {
//                throw new Exception();
//            }
//            image = ImageIO.read(imgFile);
//            new Img(image,imgFile.getName()).findClusters();
        } catch(Exception e) {
            System.out.println("Problem with clustering!: "+e);
            return;
        }
    }

    public static String[] clusterFromFolder(String path){
        ArrayList<String> results = new ArrayList<String>();
        File dir = new File(path);
        File[] files = dir.listFiles();
        int max  = 7;
        int n = 0;
        for(File f: files){
            results.add(clusterImage(f.getPath()));
            n++;
            if(n>=max){break;}
        }
        return results.toArray(new String[results.size()]);
    }

    public static String clusterImage(String path){
        Img img = null;
        File imgFile = new File(path);
        try{
            image = ImageIO.read(imgFile);
            if(imgFile.getName().matches(".*?\\.(jpg|gif|jpeg)")){
                            img = new Img(image,imgFile.getName());
                            img.findClusters();
            }
        } catch(Exception e) {
            System.out.println("error during clustering!: "+e);
        }
        return img.outputClusteringResults();
    }

    private static void outputResults(String[] res){
        File dir = new File("C:/Temp/cluster/");
        File[] files = dir.listFiles();
        for (File f : files) {  /* clear previously generated files */
            if(!f.getName().equals("style.css")){
                f.delete();
            }
        }
        int total = res.length;
        int pages = 0;
        int n = 0;
        while(n < total) {
            BufferedWriter out = null;
            try {
                FileOutputStream fstream = new FileOutputStream("C:/Temp/cluster/results-"+pages+".html");
                out = new BufferedWriter(new OutputStreamWriter(fstream, "cp1250"));
                out.write("<html><head><title>Results</title>" +
                "<LINK REL=stylesheet TYPE=\"text/css\" HREF=\"style.css\">" +
                "</head><body>");
                for(int m = 0; m < perPage; m++) {
                    try {
                        out.write(res[n]);
                    } catch(ArrayIndexOutOfBoundsException e) {
                        break;
                    }
                   catch (Exception e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                    n++;
                }
                out.write("</body></html><head>");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            } finally { /* Ensure file is closed no matter what */
                if (out != null) {
                    try {
                        out.close(); /* Close output stream */
                    } catch (Exception e) {
                        System.err.println("File Handling Error: " + e.getMessage());
                    }
                }
            }
            pages++;
        }
    }
//            } finally { /* Ensure file is closed no matter what */
//                if (out != null) {
//                    try {
//                        out.close(); /* Close output stream */
//                    } catch (Exception e) {
//                        System.err.println("Error: " + e.getMessage());
//                    }
//                }
//            }


//        for(StringBuffer result: res){
//        BufferedWriter out = null;
//            try {
//                FileOutputStream fstream = new FileOutputStream("C:/Temp/cluster/results-"+pages+".html");
//                out = new BufferedWriter(new OutputStreamWriter(fstream, "cp1250"));
//                out.write(result.toString());
//            } catch (Exception e) {
//                System.err.println("Error: " + e.getMessage());
//            } finally { /* Ensure file is closed no matter what */
//                if (out != null) {
//                    try {
//                        out.close(); /* Close output stream */
//                    } catch (Exception e) {
//                        System.err.println("Error: " + e.getMessage());
//                    }
//                }
//            }

    /**
     * display the help text
     */
    public static void showHelp() {
        System.out.println("Image Colour Analyser, Jesse Bordoe 2011\n"+
                "Provide path to jpeg image or directory to scan. If a folder is provided," +
                "all .jpeg files in the folder will be scanned");
    }

    private static void init() {
        File thumbs = new File("C:/Temp/cluster/Thumbs");
        if(!thumbs.exists()){
            thumbs.mkdir();
        }
    }
  


}
