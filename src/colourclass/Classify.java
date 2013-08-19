package colourclass;

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
    /** maximum number of images to classify in a folder */
    private static int max = 30;
    /** flags whether clustering multiple images from a folder, or a single image */
    private static boolean folder;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            parseArgs();
        } catch (Exception e) {
            System.out.print("invalid arguments provided!");
            showHelp();
            return;
        }
        /* get image path from command line args */
        try {
            String path = args[0];
            if (path.equals("-help") || path.equals("-h")) {
                showHelp();
                return;
            }
            if (path.matches(".*?\\.(jpg|gif|jpeg)")) {
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
        } catch(Exception e) {
            System.out.println("Problem with clustering!: "+e);
            return;
        }
    }

    public static void parseArgs(){

    }

    public static String[] clusterFromFolder(String path){
        ArrayList<String> results = new ArrayList<String>();
        File dir = new File(path);
        File[] files = dir.listFiles();
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
            if(imgFile.getName().matches(".*?\\.(jpg|gif|jpeg|JPG)")){
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
        String[] linkArr = makePageLinks((int)Math.ceil(res.length/perPage));
        int total = res.length;
        int currentPage = 0;
        int currentImage = 0;
        while(currentImage < total) {
            BufferedWriter out = null;
            try {
                FileOutputStream fstream = new FileOutputStream("C:/Temp/cluster/results-"+currentPage+".html");
                out = new BufferedWriter(new OutputStreamWriter(fstream, "cp1250"));
                out.write("<html><head><title>Results</title>" +
                "<LINK REL=stylesheet TYPE=\"text/css\" HREF=\"style.css\">" +
                "</head><body>");

                //Generate links to other results pages
                String links = "<b>Pages: </b>";
                for (int i = 0; i < linkArr.length; i++) {
                    String divider = i != linkArr.length-1 ? " | " : " ";
                    String link = i == currentPage ? "" + i : linkArr[i];
                    links += link + divider;
                }
                out.write(links + "<br>");
                for (int m = 0; m < perPage; m++) {
                    try {
                        out.write(res[currentImage]);
                    } catch (ArrayIndexOutOfBoundsException e) { //stop if there are no more results to show
                        break;
                    } catch (Exception e) { //Deal safely with any (unexpected) errors
                        System.err.println("Results Output Error: " + e.getMessage());
                    }
                    currentImage++;
                }
                out.write(links + "<br></body></html><head>");
            } catch (Exception e) {
                System.err.println("HTML Generation Error: " + e.getMessage());
            } finally { /* Ensure file is closed no matter what */
                if (out != null) {
                    try {
                        out.close(); /* Close output stream */
                    } catch (Exception e) {
                        System.err.println("File Handling Error: " + e.getMessage());
                    }
                }
            }
            currentPage++;
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

    private static String[] makePageLinks(int pages){
        String[] links = new String[pages];
        for(int i = 0; i < pages; i++){
            links[i] = "<a href=\"results-"+i+".html\">"+i+"</a>";
        }
        return links;
    }
  


}
