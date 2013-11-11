package colourclass;

import java.util.ArrayList;

/**
 *
 * @author Jesse Bordoe
 */
public class ClusteredImage {
    private Img sourceImage;
    private ImgCluster[] clusters;
    
    public ClusteredImage(Img img, ImgCluster[] clusters) {
        this.sourceImage = img;
        this.clusters = clusters;        
    }
    
    /**
     * @return the clusters
     */
    public ImgCluster[] getClusters() {
        return clusters;
    }

    /**
     * @return the sourceImage
     */
    public Img getSourceImage() {
        return sourceImage;
    }
}