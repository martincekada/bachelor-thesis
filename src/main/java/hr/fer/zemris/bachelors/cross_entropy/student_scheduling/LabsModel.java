package hr.fer.zemris.bachelors.cross_entropy.student_scheduling;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import hr.fer.zemris.bachelors.cross_entropy.structures.Util;
import hr.fer.zemris.bachelors.cross_entropy.student_scheduling.Solution;
import hr.fer.zemris.bachelors.cross_entropy.structures.Lab;
import hr.fer.zemris.bachelors.cross_entropy.structures.Student;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static hr.fer.zemris.bachelors.cross_entropy.structures.Util.parseLabs;
import static hr.fer.zemris.bachelors.cross_entropy.structures.Util.parseStudents;

public class LabsModel {
    private int sampleSize;
    private int Nb;
    private double smoothingParameter;
    private double overfillingCoff;
    //    private double[][] bestDistribution;
    private double[][] startDistribution;
    private double[][] currentDistribution;
    private final List<Student> students;
    private final List<Lab> labs;
    private static File stopFile = new File("./stop.txt");


    private Map<String, List<Integer>> labsAtTime = new HashMap<>();


    public LabsModel(int sampleSize, int Nb, double smoothingParameter, double[][] startDistribution,
                     List<Student> students, List<Lab> labs, double overfillingCoff) {
        this.sampleSize = sampleSize;
        this.Nb = Nb;
        this.smoothingParameter = smoothingParameter;
        this.startDistribution = startDistribution;
        this.students = students;
        this.labs = labs;
        this.overfillingCoff = overfillingCoff;


        if (this.startDistribution == null) {
            initStartDistribution();
//            this.startDistribution = new double[labs.size()][students.size()];
//
//
//            for (int i = 0, n = labs.size(); i < n; ++i) {
//                Arrays.fill(this.startDistribution[i], 1.0 / n);
//            }
        }


        for (int i = 0, n = labs.size(); i < n; ++i) {
            String labTime = Util.withoutSeconds.format(labs.get(i).getTime().getStart());

            if (!labsAtTime.containsKey(labTime)) {
                labsAtTime.put(labTime, new ArrayList<>());
            }

            labsAtTime.get(labTime).add(i);
        }
    }

    private void initStartDistribution() {

        int[][] helpDistribution = new int[labs.size()][students.size()];

        for (int i = 0, n = labs.size(); i < n; ++i) {
            for (int j = 0, m = students.size(); j < m; ++j) {
                helpDistribution[i][j] = 10;

                if (students.get(j).hasCollisionWith(labs.get(i))) {
                    helpDistribution[i][j] -= 7;
                }

                if (students.get(j).increasesDayDuration(labs.get(i))) {
                    helpDistribution[i][j] -= 1;
                }

                if (students.get(j).onFreeDay(labs.get(i))) {
                    helpDistribution[i][j] -= 1;
                }
            }
        }

        int[] sums = new int[students.size()];


        for (int i = 0, n = labs.size(); i < n; ++i) {
            for (int j = 0, m = students.size(); j < m; ++j) {
                sums[j] += helpDistribution[i][j];
            }
        }

        this.startDistribution = new double[labs.size()][students.size()];
        for (int i = 0, n = labs.size(); i < n; ++i) {
            for (int j = 0, m = students.size(); j < m; ++j) {
                startDistribution[i][j] = ((double) helpDistribution[i][j]) / sums[j];
            }
        }


    }

    public int run() throws IOException {
        currentDistribution = new double[labs.size()][students.size()];
        for (int i = 0, n = labs.size(); i < n; ++i) {
            for (int j = 0, m = students.size(); j < m; ++j) {
                currentDistribution[i][j] = startDistribution[i][j];
            }
        }


        for (int i = 0; ; ++i) {
            List<Solution> solutions = sample();
            double[][] newDistribution = evaluate(solutions);
            update(newDistribution);
            heuristics();

            if (stopCondition()) return i;
        }
    }

    public void heuristics() {

        Collection<List<Integer>> pairs = labsAtTime.values();

        for (int i = 0, n = students.size(); i < n; ++i) {

            for (List<Integer> pair : pairs) {
                double sum = 0;

                for (Integer j : pair) {
                    sum += currentDistribution[j][i];
                }

                double uniform = sum / pair.size();

                for (Integer j : pair) {
                    currentDistribution[j][i] = uniform;
                }

            }

        }

    }


    private void update(double[][] newDistribution) {
        for (int i = 0, n = labs.size(); i < n; ++i) {
            for (int j = 0, m = students.size(); j < m; ++j) {
                currentDistribution[i][j] = (1.0 - smoothingParameter) * currentDistribution[i][j] + smoothingParameter * newDistribution[i][j];
            }
        }
    }

    private double[][] evaluate(List<Solution> solutions) {
        double[][] newDistribution = new double[labs.size()][students.size()];

        solutions.sort((s1, s2) -> (Integer.compare(s1.getCost(), s2.getCost())));


        List<Solution> best = solutions.subList(0, Nb);


        System.out.println(best.get(0).getCost());

        int[][] frequences = new int[labs.size()][students.size()];


        for (int i = 0, n = best.size(); i < n; ++i) {
            int[] sequnce = best.get(i).getSequnce();

            for (int j = 0, m = sequnce.length; j < m; ++j) {
                frequences[sequnce[j]][j]++;
            }

        }


        for (int i = 0, n = labs.size(); i < n; ++i) {
            for (int j = 0, m = students.size(); j < m; ++j) {
                newDistribution[i][j] = ((double) frequences[i][j]) / Nb;
            }
        }


        return newDistribution;
    }

    private List<Solution> sample() {
        return sample(sampleSize);
    }

    private List<Solution> sample(int samples) {
        List<Solution> solutions = new ArrayList<>(samples);

        Random rand = new Random();
        for (int i = 0; i < samples; ++i) {
            int[] sample = new int[students.size()];

            for (int j = 0, m = students.size(); j < m; ++j) {
                double possibility = 0;
                double generated = rand.nextDouble();

                for (int k = 0, n = labs.size(); k < n; k++) {
                    possibility += currentDistribution[k][j];
                    if (generated < possibility) {
                        sample[j] = k;
                        break;
                    }
                }

            }


            solutions.add(new Solution(sample, costFunction(sample)));
        }


        return solutions;
    }


    private int costFunction(int[] sample) {
        int cost = 0;

        int[] filled = new int[labs.size()];

        for (int i = 0, n = students.size(); i < n; ++i) {
            if (students.get(i).hasCollisionWith(labs.get(sample[i]))) {
                cost += 25_000;
            }

            if (students.get(i).increasesDayDuration(labs.get(sample[i]))) {
                cost += 50;
            }

            if (students.get(i).onFreeDay(labs.get(sample[i]))) {
                cost += 50;
            }


            filled[sample[i]]++;
        }

        for (int i = 0, n = labs.size(); i < n; ++i) {
            if (filled[i] > labs.get(i).getMaxStudents()) {

                // ovdje ne uzimati linearno, nego kvadratno + koeficijine
//                1000 + koff * delta^2 (koff moze komotno biti velik broj, 100/1000)
                cost += (5000 + overfillingCoff * Math.pow((filled[i] - labs.get(i).getMaxStudents()), 2));
            }

            if (filled[i] < labs.get(i).getMinStudents()) {
//                System.out.println("Manje od min");

                // ovdje ne uzimati linearno, nego kvadratno + koeficijine
//                1000 + koff * delta^2 (koff moze komotno biti velik broj, 100/1000)

                cost += (5000 + overfillingCoff * Math.pow((labs.get(i).getMinStudents() - filled[i]), 2));
            }

//            if (filled[i] < labs.get(i).getMaxStudents() * 0.8) {
//                cost += 5;
//            }

//            if (filled[i] == labs.get(i).getMaxStudents()) {
//                cost -= 100;
//            }

        }

        return cost;
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

    private boolean stopCondition() throws IOException {
        if (stopFile.exists()) {
            int colissionCounter = 0;
            List<Solution> solutions = sample();
            solutions.sort((s1, s2) -> (Integer.compare(s1.getCost(), s2.getCost())));
            int[] solution = solutions.get(0).getSequnce();

            File file = new File(Util.withoutSeconds.format(new Date()));
            file.createNewFile();


            PrintWriter writer = new PrintWriter(file);

            writer.println("Smoothing parameter: " + smoothingParameter);
            writer.println("Sample size: " + sampleSize);
            writer.println("Best: " + Nb);


            for (int i = 0, n = students.size(); i < n; ++i) {

                students.get(i).setLab(labs.get(solution[i]));

                if (students.get(i).hasCollision()) {
                    colissionCounter++;
                }


                System.out.println(students.get(i).printLabDay());
                System.out.println("------------------------------------------------");
                writer.println(students.get(i).printLabDay());
                writer.println("------------------------------------------------");
            }


            int[] filled = new int[labs.size()];

            for (int i = 0, n = students.size(); i < n; ++i) {
                filled[solution[i]]++;
            }

            int overfilled = 0;
            for (int i = 0, n = labs.size(); i < n; ++i) {
                if (labs.get(i).getMaxStudents() < filled[i]) {
                    overfilled++;
                }
                System.out.println("Za labos " + " " + labs.get(i).getName() + " je rasporedjeno " + filled[i]);
                writer.println("Za labos " + " " + labs.get(i).getName() + " je rasporedjeno " + filled[i]);
            }

            System.out.println("Ukupan broj kolizija: " + colissionCounter);
            System.out.println("Ukupan broj prepunjenih: " + overfilled);
            System.out.println("Score: " + solutions.get(0).getCost());

            writer.println("Ukupan broj kolizija: " + colissionCounter);
            writer.println("Ukupan broj prepunjenih: " + overfilled);
            writer.println("Score: " + solutions.get(0).getCost());

            writer.println("Smoothing parameter: " + smoothingParameter);
            writer.println("Sample size: " + sampleSize);
            writer.println("Best: " + Nb);

            return true;
        }
        return false;
    }



//    BILJESKE S SASTANKA

//    dva reda velicine razlike izmedju hard contraintova i soft (1000 za kolizijie i prepunjavanja, 10 za produzivanje dana i slicno.)
//    kaznjavati prepunjavanje kvadratno i drakonski (kao i kolizije)
//    smanjiti sampleSize na 100, 200, 300 (ne vise od toga)
//    heuristike: uplesti se u proces generiranja nove vjerojatnosti
//    nakon sto je generirana nova vjerojatnost, vidjeti koji su termini prepunjeni i onda njima smanjiti sanse
//    !! ne zapoceti od uniformne razdiobe, nego na pocetku vidjeti koji termini studentu produzuju dan i s kojima ima kolizije i na
//    temelju toga odrediti pocetnu razdiobu
//    1. heuristika - nakon sto oderedimnovu razdiobu, ja znam koji su mi termini prepunjeni, na temelju toga tim terminima smanjim vjerojatnost
//    2. heuristika - znam i koji su mi termini podpunjeni, njima mogu povezati vjerojatnost
//    -> za jednostavnije racunanje, pomnoziti prepunjene (ili potpunjene) termine s neki koff i onda normalizirati cijelu matricu (mozda prvo tu matricu
//    prevaciti u neku sa integerima)



    public static void main(String[] args) throws IOException {
        List<Lab> labs = parseLabs(         "./src/main/resources/primjeri/primjer6/termini.txt", true);
        List<Student> studs = parseStudents("./src/main/resources/primjeri/primjer6/zauzetost.csv");

        LabsModel model = new LabsModel(300, 50, 0.8, null, studs, labs, 2000);

        model.run();

    }

}




















