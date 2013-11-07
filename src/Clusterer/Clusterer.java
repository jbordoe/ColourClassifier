package Clusterer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class Clusterer {

    public static final int CENTERS_FROM_DATASET = 0;
    public static final int CENTERS_AT_RANDOM = 1;
    public int centerSelection = CENTERS_FROM_DATASET;
    private static final Random rand = new Random();
    
    private double[][] dataPoints;
    private int dimensions;
    private int datasetSize;
    /**
     * Number of clusters to generate from dataset
     */
    private int k = 4;

    public Clusterer(double[][] dataPoints)  {
        this.dataPoints = dataPoints;
        this.dimensions = dataPoints[0].length;
        this.datasetSize = dataPoints.length;        
    }

    public Cluster[] findClusters() {

        double[][] centers = initializeCenters();
        int[] assignments = assignDataPoints(centers);

        boolean clustersConverged = false;
        int runs = 0;

        while (!clustersConverged) {
            runs ++;            
            centers = moveCenters(assignments);
            int[] newAssignments = assignDataPoints(centers);
            clustersConverged = !assignmentsHaveChanged(assignments, newAssignments);
            assignments = newAssignments;
        }
        Cluster[] clusters = new Cluster[k];
        return clusters;
    }

    /**
     *
     */
    private double[][] initializeCenters() {
        switch (centerSelection) {
            case CENTERS_FROM_DATASET: {
                return centersFromDataset();
            }
            case CENTERS_AT_RANDOM: {
                return centersAtRandom();
            }
            default: {
                return centersFromDataset();
            }
        }
    }

    private double[][] centersFromDataset() {       
        double[][] centers = new double[k][dimensions];
        int i = 0;
        HashSet<Integer> indexes = new HashSet<Integer>();
        while (i < k) {
            int n = rand.nextInt(datasetSize);            
            if (!indexes.contains(n)) {
                double[] center = dataPoints[n].clone();
                centers[i] = center;
                indexes.add(n);
            }            
        }
        return centers;
    }
    
    private double[][] centersAtRandom() {
        /*
         * TODO: this is a no-op! 
         * This is generally an inferior technique 
         * for initialising clusters, but do it anyway!
         */
        return null;
    }
    
    private double[][] moveCenters (int[] assignments) {
        double[][] centers = new double[k][dimensions];
        
        int[] clusterSizes = new int[k];
        
        for (int i = 0; i < datasetSize; i++) {
            int centerIndex = assignments[i];
            clusterSizes[i] += 0;
            for (int j = 0; j < dimensions; j++) {
                centers[centerIndex][j] += dataPoints[i][j];
            }
        }
        
        for (int i = 0; i < k; i ++) {
            double[] center = centers[i];
            int clusterSize = clusterSizes[i];
            for (int j = 0; j < dimensions; j++) {
                center[j] = center[j] / clusterSize;
            }
        }        
        return centers;
    }
    
    private boolean assignmentsHaveChanged(int[] oldAssignments, int[] newAssignments) {
        for (int i = 0; i < datasetSize; i++) {
            if ( oldAssignments[i] != newAssignments[i]) {
                return true;
            }
        }        
        return false;
    }
    
    private int[] assignDataPoints(double[][] centers) {
        int[] assignments = new int[datasetSize];
        
        for (int i = 0; i < datasetSize; i++) {
            double[] dataPoint = dataPoints[i];
            assignments[i] = getIndexOfClosestCenter(dataPoint, centers);            
        }
        return assignments;
    }
    
    private int getIndexOfClosestCenter(double[] dataPoint, double[][] centers) {
        int indexOfClosest = -1;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            double[] center = centers[i];
            double dist = getEuclidianDistance(dataPoint, center);
            if (indexOfClosest == -1 || dist < closestDistance) {
                indexOfClosest = i;
            }
        }
        return indexOfClosest;
    }
    
    /**
     * calculate euclidian distance between two n-dimensional data points
     * @param dataPoint1 first data point
     * @param dataPoint2 second data point
     * @return euclidian distance between data point 1 and data point 2
     */
    private double getEuclidianDistance(double[] dataPoint1, double[] dataPoint2) { 
        double sumOfSquaredDifferences = 0;
        for (int i = 0; i < dimensions; i++) {
            sumOfSquaredDifferences += Math.pow(dataPoint1[i] - dataPoint2[i], 2);
        }
        return Math.sqrt(sumOfSquaredDifferences);
    }
    

    /**
     * @return the number of clusters to generate
     */
    public int getK() {
        return k;
    }

    /**
     * @param the number of clusters to generate
     */
    public void setK(int k) {
        this.k = k;
    }
}
