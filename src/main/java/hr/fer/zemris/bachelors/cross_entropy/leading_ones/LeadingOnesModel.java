package hr.fer.zemris.bachelors.cross_entropy.leading_ones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LeadingOnesModel {
    private final int MAX_ITERATIONS = 500_000;
    private int L = 30;
    private static final char[] ALPHABET = new char[] {'1', '0'};
    private int sampleSize;
    private int Nb;
    private double smoothingParameter;
    private double[][] startDistribution;
    private double[][] currentDistribution;
    private double stopCoff;

    public LeadingOnesModel(int sampleSize, int Nb, int L, double smoothingParameter, double[][] startDistribution, double stopCoff) {
        this.sampleSize = sampleSize;
        this.Nb = Nb;
        this.smoothingParameter = smoothingParameter;
        this.startDistribution = startDistribution;
        this.L = L;
        this.stopCoff = stopCoff;

        if (this.startDistribution == null) {
            this.startDistribution = new double[2][L];

            Arrays.fill(this.startDistribution[0], 0.5);
            Arrays.fill(this.startDistribution[1], 0.5);
        }
    }

    public int run() {
        currentDistribution = new double[2][L];
        for (int i = 0; i < 2; ++i) {
            for(int j = 0; j < L; ++j) {
                currentDistribution[i][j] = startDistribution[i][j];
            }
        }


        for (int i = 0;; ++i) {
            List<Solution> solutions = sample();
            double[][] newDistribution = evaluate(solutions);
            update(newDistribution);

            if (stopCondition()) return i;
        }
    }


    private boolean stopCondition() {
        for (int i = 0; i < L; ++i) {
            if (currentDistribution[1][i] < stopCoff) {
                return false;
            }
        }

        return true;
    }

    private void update(double[][] newDistribution) {
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < L; ++j) {
                currentDistribution[i][j] = (1.0 - smoothingParameter) * currentDistribution[i][j] + smoothingParameter * newDistribution[i][j];
            }
        }
    }

    private double[][] evaluate(List<Solution> solutions) {
        double[][] newDistribution = new double[2][L];

        solutions.sort((s1, s2) -> (Integer.compare(s1.getCost(), s2.getCost())));

        List<Solution> best = solutions.subList(0, Nb);


        int[] zeroFrequences = new int[L];

        for (Solution s : best) {
            int i = 0;
            for (char c : s.getSequnce().toCharArray()) {
                if (c == '0') zeroFrequences[i]++;
                ++i;
            }
        }


        for (int i = 0; i < L; ++i) {
            double zeroProbability = ((double) zeroFrequences[i]) / Nb;
            newDistribution[0][i] = zeroProbability;
            newDistribution[1][i] = 1 - zeroProbability;
        }

        return newDistribution;
    }

    private List<Solution> sample() {
        List<Solution> solutions = new ArrayList<>(sampleSize);

        for (int i = 0; i < sampleSize; ++i) {
            StringBuilder s = new StringBuilder();
            Random rand = new Random();

            for (int j = 0; j < L; ++j) {
                if (rand.nextDouble() < currentDistribution[0][j]) {
                    s.append('0');
                } else {
                    s.append('1');
                }
            }

            solutions.add(new Solution(s.toString(), costFunction(s.toString())));
        }


        return solutions;
    }

    private int costFunction(String solution) {
        if (!solution.contains("0")) return  0;

        return L - solution.indexOf('0');
    }

    public String getL() {
        return String.valueOf(L);
    }

    public String getSampleSize() {
        return String.valueOf(sampleSize);
    }

    public String getNb() {
        return String.valueOf(Nb);
    }

    public String getSmoothingParameter() {
        return String.valueOf(smoothingParameter);
    }

    public double[][] getCurrentDistribution() {
        return currentDistribution;
    }

    public String getStopCoff() {
        return String.valueOf(stopCoff);
    }
}




















