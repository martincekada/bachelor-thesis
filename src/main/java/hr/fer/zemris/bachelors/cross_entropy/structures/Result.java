package hr.fer.zemris.bachelors.cross_entropy.structures;

public class Result {
    private int N;
    private int Nb;
    private double smoothingParam;
    private int qSize;
    private double qFactor;
    private int overfilled;
    private int collisions;
    private int cost;
    private double overfillingCoff;

    public Result(int n, int nb, double smoothingParam, int qSize, double qFactor, int overfilled, int collisions, int cost, double overfillingCoff) {
        N = n;
        Nb = nb;
        this.smoothingParam = smoothingParam;
        this.qSize = qSize;
        this.qFactor = qFactor;
        this.overfilled = overfilled;
        this.collisions = collisions;
        this.cost = cost;
        this.overfillingCoff = overfillingCoff;
    }

    public int getN() {
        return N;
    }

    public int getNb() {
        return Nb;
    }

    public double getSmoothingParam() {
        return smoothingParam;
    }

    public int getqSize() {
        return qSize;
    }

    public double getqFactor() {
        return qFactor;
    }

    public int getOverfilled() {
        return overfilled;
    }

    public int getCollisions() {
        return collisions;
    }

    public int getCost() {
        return cost;
    }

    public double getOverfillingCoff() {
        return overfillingCoff;
    }
}
