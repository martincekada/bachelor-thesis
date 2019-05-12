package hr.fer.zemris.bachelors.cross_entropy.structures;

import hr.fer.zemris.bachelors.cross_entropy.student_scheduling.Solution;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class SolutionsQueue {
    private PriorityQueue<Solution> solutions;
    private int size;
    private boolean updated;
    private boolean updatedF;
    private double[][] distribution;
    private int studentsSize;
    private int labsSize;
    private Map<Integer, Integer> overFilledLabs;
    private int labsMaxSize;

    private int updateCounter;


    public SolutionsQueue(int size, int studentsSize, int labsSize, int labsMaxSize) {
        this.size = size;
        this.updated = true;
        this.solutions = new PriorityQueue<>((s1, s2) -> Integer.compare(s2.getCost(), s1.getCost()));
        this.labsSize = labsSize;
        this.studentsSize = studentsSize;
        this.distribution = new double[labsSize][studentsSize];
        this.labsMaxSize = labsMaxSize;
        this.overFilledLabs = new HashMap<>();
    }

    public double[][] getDistribution() {
        if (updated) {
            recalculateDistribution();
            updated = false;
        }

        return distribution;
    }

    public void add(Solution candidate) {
//        System.out.println(solutions.peek());
        if (solutions.size() < size) {
            solutions.add(candidate);
            updated = true;
            updatedF = true;
//            updateCounter++;
        } else {
            if (solutions.peek().getCost() > candidate.getCost()) {

                solutions.poll();
                solutions.add(candidate);
                updated = true;
                updatedF = true;
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

    public Map<Integer, Integer> getOverfilled() {
        if (updatedF) {
            recalculateOverilled();
            updatedF = false;
        }

        return overFilledLabs;
    }

    public void recalculateOverilled() {
        Solution[] sulutionArray = solutions.toArray(new Solution[0]);

        overFilledLabs.clear();

        overFilledLabs = new HashMap<>();
        int[] overfilledCount = new int[labsSize];


        for (Solution s : sulutionArray) {
            int[] filled = new int[labsSize];

            for (int i = 0, n = studentsSize; i < n; ++i) {
                filled[s.getSequnce()[i]]++;
            }


            for (int i = 0, n = labsSize; i < n; ++i) {
                overfilledCount[i] = filled[i] - labsMaxSize;

                if (filled[i] - labsMaxSize > 0) {
                    if (overFilledLabs.containsKey(i)) {
                        overFilledLabs.put(i, overFilledLabs.get(i) + 1);
                    } else {
                        overFilledLabs.put(i, 1);
                    }
                }
            }
        }

    }


    public void clear() {
        solutions.clear();
    }

}
