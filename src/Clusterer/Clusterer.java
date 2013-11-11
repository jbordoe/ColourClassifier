package Clusterer;

import java.util.ArrayList;
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
    private boolean verbose = false;
    /**
     * Number of clusters to generate from dataset
     */
    private int k = 4;

    public Clusterer(double[][] dataPoints) {
        this.dataPoints = dataPoints;
        this.dimensions = dataPoints[0].length;
        this.datasetSize = dataPoints.length;
    }

    /**
     * Run the k-means clustering algorithm on the dataset we initialised with
     *
     * @return array of Cluster objects
     */
    public Cluster[] findClusters() {

        say("Initializing centers...");
        double[][] centers = initializeCenters();
        say("Assinging initial clusters");
        int[] assignments = assignDataPoints(centers);

        boolean clustersConverged = false;
        int runs = 0;

        while (!clustersConverged) {
            runs++;
            say("Run: " + runs);
            say("Moving centers...");
            centers = moveCenters(assignments);
            say("Reassingning...");
            int[] newAssignments = assignDataPoints(centers);
            say("Checking for convergence...");
            clustersConverged = !assignmentsHaveChanged(assignments, newAssignments);
            assignments = newAssignments;
        }
        say("Converged!");
        Cluster[] clusters = new Cluster[k];

        for (int i = 0; i < k; i++) {
            double[][] clusterDataPoints = filterDataPoints(dataPoints, assignments, i);
            Cluster cluster = new Cluster(centers[i], clusterDataPoints);
            clusters[i] = cluster;
        }
        return clusters;
    }

    /**
     * Obtain a list of all data points mapped to a certain cluster/center
     *
     * @param dataPoints array of n-dimensional data points
     * @param assignments
     * @param label index of cluster whose data points we wish to retrieve
     * @return
     */
    private double[][] filterDataPoints(double[][] dataPoints, int[] assignments, int label) {
        ArrayList<double[]> filteredDataPoints = new ArrayList<double[]>();

        for (int i = 0; i < assignments.length; i++) {
            if (assignments[i] == label) {
                filteredDataPoints.add(dataPoints[i]);
            }
        }
        return filteredDataPoints.toArray(new double[filteredDataPoints.size()][dimensions]);
    }

    /**
     * Use one of various methods to select data points to be used as 'centers'
     * for our initialisation of the clustering algorithm
     *
     * @return array of centers (each center is an n-dimensional array of
     * doubles)
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

    /**
     * Select given number of centers from existing data points in our dataset
     *
     * @return
     */
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
                i++;
            }
        }
        return centers;
    }

    /**
     * Select given number of centers at random from within defined space
     *
     * @return
     */
    private double[][] centersAtRandom() {
        /*
         * TODO: this is a no-op! 
         * This is generally an inferior technique 
         * for initialising clusters, but do it anyway!
         */
        return null;
    }

    /**
     * Move cluster centers according to data points in each cluster: center
     * should be the average value of all datapoints in it's cluster
     *
     * @param assignments
     * @return
     */
    private double[][] moveCenters(int[] assignments) {
        double[][] centers = new double[k][dimensions];

        int[] clusterSizes = new int[k];

        for (int i = 0; i < datasetSize; i++) {
            int centerIndex = assignments[i];
            clusterSizes[centerIndex] += 1;
            for (int j = 0; j < dimensions; j++) {
                centers[centerIndex][j] += dataPoints[i][j];
            }
        }

        for (int i = 0; i < k; i++) {
            double[] center = centers[i];
            int clusterSize = clusterSizes[i];
            for (int j = 0; j < dimensions; j++) {
                center[j] = center[j] / clusterSize;
            }
        }
        return centers;
    }

    /**
     * Determine whether assignments of data points to clusters has changed over
     * an iteration
     *
     * @param oldAssignments assignments from previous iteration
     * @param newAssignments assignments from this iteration
     * @return true if assignments have changed, false if not
     */
    private boolean assignmentsHaveChanged(int[] oldAssignments, int[] newAssignments) {
        for (int i = 0; i < datasetSize; i++) {
            if (oldAssignments[i] != newAssignments[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a list of centers, assign each data point to it's closest center
     *
     * @param centers array of centers (which are themselves n-dimensional
     * arrays)
     * @return
     */
    private int[] assignDataPoints(double[][] centers) {
        int[] assignments = new int[datasetSize];

        for (int i = 0; i < datasetSize; i++) {
            double[] dataPoint = dataPoints[i];
            assignments[i] = getIndexOfClosestCenter(dataPoint, centers);
        }
        return assignments;
    }

    /**
     * Given an array of centers, return index of center with smallest euclidian
     * distance from data point
     *
     * @param dataPoint n-dimensional array
     * @param centers array of data points
     * @return
     */
    private int getIndexOfClosestCenter(double[] dataPoint, double[][] centers) {
        int indexOfClosest = -1;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            double[] center = centers[i];
            double dist = getEuclidianDistance(dataPoint, center);
            if (indexOfClosest == -1 || dist < closestDistance) {
                indexOfClosest = i;
                closestDistance = dist;
            }
        }
        return indexOfClosest;
    }

    /**
     * calculate euclidian distance between two n-dimensional data points
     *
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
     * Output given string only if in verbose mode
     *
     * @param s the String to print
     */
    private void say(String s) {
        if (verbose) {
            System.out.println(s);
        }
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

    /**
     * @param verbose flags whether in verbose mode (see 'say(String s)' )
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
