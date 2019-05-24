package hr.fer.zemris.bachelors.cross_entropy.student_scheduling;

import com.opencsv.CSVWriter;
import hr.fer.zemris.bachelors.cross_entropy.leading_ones.LeadingOnesModel;
import hr.fer.zemris.bachelors.cross_entropy.structures.Lab;
import hr.fer.zemris.bachelors.cross_entropy.structures.Result;
import hr.fer.zemris.bachelors.cross_entropy.structures.Student;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import static hr.fer.zemris.bachelors.cross_entropy.structures.Util.parseLabs;
import static hr.fer.zemris.bachelors.cross_entropy.structures.Util.parseStudents;

public class LabsEvaluator {
    private List<LabsModel> models;
    private String path;
    private int noTests;

    public LabsEvaluator(List<LabsModel> models, String path, int noTests) throws IOException {
        this.models = models;
        this.path = path;
        this.noTests = noTests;

        File file = new File(path);

        FileWriter outputFile = new FileWriter(file, true);

        CSVWriter writer = new CSVWriter(outputFile);

        String[] header = { "N", "Nb", "smoothingParam", "qSize", "qFactor", "overFilledCoff", "Overfilled", "Kolizije", "median",
                "avg", "min", "max"
        };
        writer.writeNext(header);
        writer.flush();
        writer.close();
    }


    public void generateStats() throws IOException {

        for (LabsModel model : models) {
            System.out.println("+");

            List<Result> results = new ArrayList<>(noTests);

            for (int i = 0; i < noTests; ++i) {
                results.add(model.run());
            }

            results.sort((r1, r2) -> Integer.compare(r1.getCost(), r2.getCost()));

            String median = String.valueOf(results.get(noTests / 2).getCost());

            IntSummaryStatistics stats = results.stream()
                                                .collect(Collectors.summarizingInt(Result::getCost));



            writeStats(results.get(0), median, stats);
        }
    }


    private void writeStats(Result result, String median, IntSummaryStatistics stats) {
        File file = new File(path);
        try {

            FileWriter outputFile = new FileWriter(file, true);

            CSVWriter writer = new CSVWriter(outputFile);

            String[] row = { String.valueOf(result.getN()), String.valueOf(result.getNb()), String.valueOf(result.getSmoothingParam()),
                    String.valueOf(result.getqSize()), String.valueOf(result.getqFactor()), String.valueOf(result.getOverfillingCoff()),
                    String.valueOf(result.getOverfilled()), String.valueOf(result.getCollisions()), median,
                    String.valueOf(stats.getAverage()), String.valueOf(stats.getMin()), String.valueOf(stats.getMax())
            };

            writer.writeNext(row);

            // closing writer connection
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        List<Lab> labs = parseLabs(         "./src/main/resources/primjeri/primjer6/termini.txt", true);
        List<Student> studs = parseStudents("./src/main/resources/primjeri/primjer6/zauzetost.csv");


        int[] Ns =  { 100, 150, 200, 250, 300 };
        int[] Nbs = { 50,  50,  50,  50,  50  };

        List<LabsModel> models = new ArrayList<>();

        for (int i = 0; i < 2; ++i) {


            models.add(new LabsModel(
                    Ns[i], Nbs[i], 0.5, null, studs, labs, 10_000,
                    50, 0.3
            ));
        }

        LabsEvaluator evaluator = new LabsEvaluator(models, "/Users/infinum/Desktop/test.csv", 30);

        evaluator.generateStats();
    }


}
