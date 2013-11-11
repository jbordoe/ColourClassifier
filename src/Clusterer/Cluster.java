package Clusterer;

public class Cluster {
    
    private double[][] dataPoints;
    private double[] center;
    
    public Cluster(double[] center, double[][] dataPoints) {
        this.dataPoints = dataPoints;
        this.center = center;    
    }

    /**
     * @return the center of this cluster
     */
    public double[] getCenter() {
        return center;
    }

    /**
     * @return the dataPoints in this cluster
     */
    public double[][] getDataPoints() {
        return dataPoints;
    }
    
    

}