package hr.fer.zemris.bachelors.cross_entropy.leading_ones;

import com.opencsv.CSVWriter;

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
        final double STOP_COFF = 0.999;


        double[] smoothingParameters = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };

        double[] bete = new double[10];

        int j = 0;
        for (double d : smoothingParameters) {
            double product = 1;

            for (int i = 1; i < 500; ++i) {
                product *= (1 - Math.pow((1-d), i));
            }

//            System.out.println(product / (3 * Math.E));

            bete[j++] = (product / (3 * Math.E)) * 0.9;
//            System.out.println("b: " + bete[j-1]);
        }

        List<LeadingOnesModel> models = new ArrayList<>();

//        int[] ls = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

        for (int i = 0; i < 10; ++i) {
            int n = (int) Math.pow(50, 2.5);
            int nb = (int) (Math.pow(50, 2.5) * bete[i]);
            models.add(new LeadingOnesModel(n, nb, 50, smoothingParameters[i], null, STOP_COFF));
        }




//        List<LeadingOnesModel> models = new ArrayList<>();
//
//        int[] ls = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
//
//        for (int l : ls) {
//            int n = (int) Math.pow(l, 2.5);
//            int nb = (int) (Math.pow(l, 2.5) * 0.09);
//            models.add(new LeadingOnesModel(n, nb, l, 0.8, null, STOP_COFF));
//        }
//

//
//
//
//
//
        Evaluator evaluator = new Evaluator(models, "/Users/infinum/Desktop/example3.csv", 30);
        evaluator.generateStats();
    }
}
