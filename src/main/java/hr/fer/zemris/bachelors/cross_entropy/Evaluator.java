package hr.fer.zemris.bachelors.cross_entropy;

import com.opencsv.CSVWriter;
import hr.fer.zemris.bachelors.cross_entropy.leading_ones.LeadingOnesModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Evaluator {
    private List<LeadingOnesModel> models;
    private String path;
    private int noTests;

    public Evaluator(List<LeadingOnesModel> models, String path, int noTests) {
        this.models = models;
        this.path = path;
        this.noTests = noTests;
    }

    public void generateStats() {

        List<String[]> stats = new ArrayList<>();

        for (LeadingOnesModel model : models) {
            System.out.println("+");
            int[] iterations = new int[noTests];

            for (int i = 0; i < noTests; ++i) {
                iterations[i] = model.run();
            }
            Arrays.sort(iterations);

            String iters = String.valueOf(iterations[noTests / 2]);

//            stats.add(new String[] {model.getL(), model.getSampleSize(), model.getNb(), model.getSmoothingParameter(), iters, model.getStopCoff()});

            writeStats(new String[] {model.getL(), model.getSampleSize(), model.getNb(), model.getSmoothingParameter(), iters, model.getStopCoff()});
        }



//        writeStats(stats);
    }


    private void writeStats(List<String[]> stats) {
        File file = new File(path);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputFile = new FileWriter(file, true);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputFile);

//             adding header to csv
            String[] header = { "L", "N", "Nb", "Q", "i", "stopCoff" };
            writer.writeNext(header);

            // add data to csv
//            writer.writeAll(stats);

            for (String[] s : stats) {
                writer.writeNext(s);
            }
            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStats(String[] stats) {
        File file = new File(path);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputFile = new FileWriter(file, true);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputFile);

//             adding header to csv
//            String[] header = { "L", "N", "Nb", "Q", "i", "stopCoff" };
//            writer.writeNext(header);

            // add data to csv
//            writer.writeAll(stats);

            writer.writeNext(stats);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        List<LeadingOnesModel> models = new ArrayList<>();

        int[] ls = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

        for (int l : ls) {
            int n = (int) Math.pow(l, 2.5);
            int nb = (int) (Math.pow(l, 2.5) * 0.1);
            models.add(new LeadingOnesModel(n, nb, l, 0.8, null, 0.9));
        }

        for (int l : ls) {
            int n = (int) Math.pow(l, 2.5) / 2;
            int nb = (int) (Math.pow(l, 2.5) * 0.1) / 2;
            models.add(new LeadingOnesModel(n, nb, l, 0.8, null, 0.9));
        }

        Evaluator evaluator = new Evaluator(models, "/Users/infinum/Desktop/example.csv", 30);
        evaluator.generateStats();
    }
}
