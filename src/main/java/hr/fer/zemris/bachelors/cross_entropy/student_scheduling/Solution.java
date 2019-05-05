package hr.fer.zemris.bachelors.cross_entropy.student_scheduling;

import java.util.Arrays;

public class Solution {
    private int[] sequnce;
    private int cost;

    public Solution(int[] sequnce, int cost) {
        this.sequnce = sequnce;
        this.cost    = cost;
    }

    public int[] getSequnce() {
        return sequnce;
    }

    public int getCost() {
        return cost;
    }



    @Override
    public String toString() {
        return Arrays.toString(sequnce) + " " + cost;
    }
}
