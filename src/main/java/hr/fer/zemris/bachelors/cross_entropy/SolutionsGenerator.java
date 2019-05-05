package hr.fer.zemris.bachelors.cross_entropy;

import java.util.*;

public class SolutionsGenerator {
    private int n;
    private int length;

    public SolutionsGenerator(int n, int lenght) {
        this.n = n;
        this.length = lenght;
    }


    public List<String> generate() {
        Set<String> set = new HashSet<>(n);

        while (set.size() < n) {
            set.add(generateSolution());
        }

        return new ArrayList<>(set);
    }

    private String generateSolution() {
        char[] s = new char[length];
        Random rand = new Random();

        for (int i = 0; i < length; ++i) {
            if (rand.nextBoolean()) {
                s[i] = '1';
            } else {
                s[i] = '0';
            }
        }

        return new String(s);
    }


    public static void main(String[] args) {
        SolutionsGenerator a = new SolutionsGenerator(10, 20);

        List<String> b = a.generate();

        for (String s : b) {
            System.out.println(s);
        }
    }
}




















