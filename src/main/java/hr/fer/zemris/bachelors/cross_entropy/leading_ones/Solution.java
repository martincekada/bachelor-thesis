package hr.fer.zemris.bachelors.cross_entropy.leading_ones;

public class Solution {
    private String sequnce;
    private int cost;

    public Solution(String sequnce, int cost) {
        this.sequnce = sequnce;
        this.cost    = cost;
    }

    public String getSequnce() {
        return sequnce;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return sequnce + " " + cost;
    }
}
