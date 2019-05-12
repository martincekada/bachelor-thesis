package hr.fer.zemris.bachelors.cross_entropy.structures;

import hr.fer.zemris.bachelors.cross_entropy.student_scheduling.Solution;

import java.util.List;
import java.util.PriorityQueue;

public class SolutionsQueue {
    private PriorityQueue<Solution> solutions;
    private int size;
    private boolean updated;
    private double[][] distribution;
    private int studentsSize;
    private int labsSize;

    private int updateCounter;


    public SolutionsQueue(int size, int studentsSize, int labsSize) {
        this.size = size;
        this.updated = true;
        this.solutions = new PriorityQueue<>((s1, s2) -> Integer.compare(s2.getCost(), s1.getCost()));
        this.labsSize = labsSize;
        this.studentsSize = studentsSize;
        this.distribution = new double[labsSize][studentsSize];
    }

    public double[][] getDistribution() {
        if (updated) {
            recalculateDistribution();
            updated = false;
//            System.out.println(updateCounter);
            updateCounter = 0;


        }



        return distribution;
    }

    public void add(Solution candidate) {
//        System.out.println(solutions.peek());
        if (solutions.size() < size) {
            solutions.add(candidate);
            updated = true;
//            updateCounter++;
        } else {
            if (solutions.peek().getCost() > candidate.getCost()) {

                solutions.poll();
                solutions.add(candidate);
                updated = true;
//                updateCounter++;

            }
        }
    }


    public void recalculateDistribution() {


        int[][] frequences = new int[labsSize][studentsSize];

        Solution[] sulutionArray = solutions.toArray(new Solution[0]);

        for (int i = 0, n = solutions.size(); i < n; ++i) {
            int[] sequnce = sulutionArray[i].getSequnce();

            for (int j = 0, m = sequnce.length; j < m; ++j) {
                frequences[sequnce[j]][j]++;
            }

        }


        for (int i = 0, n = labsSize; i < n; ++i) {
            for (int j = 0, m = studentsSize; j < m; ++j) {
                distribution[i][j] = ((double) frequences[i][j]) / solutions.size();
            }
        }
    }


    public void clear() {
        solutions.clear();
    }

}
