package hr.fer.zemris.bachelors.cross_entropy.student_scheduling;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import hr.fer.zemris.bachelors.cross_entropy.structures.*;
import hr.fer.zemris.bachelors.cross_entropy.student_scheduling.Solution;
import org.apache.commons.lang3.time.StopWatch;

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
    private int queueSize;
    private double queueFactor;
    private SolutionsQueue queue;
    private Solution bestSolution = new Solution(new int[0], Integer.MAX_VALUE);


    private Map<String, List<Integer>> labsAtTime = new HashMap<>();


    public LabsModel(int sampleSize, int Nb, double smoothingParameter, double[][] startDistribution,
                     List<Student> students, List<Lab> labs, double overfillingCoff, int queueSize, double queueFactor) {
        this.sampleSize = sampleSize;
        this.Nb = Nb;
        this.smoothingParameter = smoothingParameter;
        this.startDistribution = startDistribution;
        this.students = students;
        this.labs = labs;
        this.overfillingCoff = overfillingCoff;
        this.queueFactor = queueFactor;
        this.queueSize = queueSize;
        this.queue = new SolutionsQueue(queueSize, students.size(), labs.size(), labs.get(0).getMaxStudents());



        if (this.startDistribution == null) {
            initStartDistribution();
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
                    helpDistribution[i][j] -= 8;
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

    public Result run() throws IOException {
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

//            if (stopCondition()) return i;


            if (i > 3000) {
//                stopCondition();
                return generateResult();
            }


            if ((i + 1) % 50 == 0) {

                queue.clear();
                queue.add(bestSolution);
//                System.out.println("---cistim");

            }
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
//
//        int[] sample = sample(1).get(0).getSequnce();
//        int[] filled = new int[labs.size()];
//
//        for (int i = 0, n = students.size(); i < n; ++i) {
//            filled[sample[i]]++;
//        }

        Map<Integer, Integer> overFilledLabs = queue.getOverfilled();
        Integer sum = overFilledLabs.values().stream().mapToInt(Integer::intValue).sum();


        if (sum > 300) return;


//        System.out.println(sum);
//        System.out.println("overfillani su: " + overFilledLabs.keySet().toString());
//        System.out.println("=========redistribuiram===========");

//        double UPPER_BOUND = 0.05;
//        double LOWWER_BOUND = 0.01;
//        int maxi = overFilledLabs.values().stream().mapToInt(Integer::intValue).max().getAsInt();
//
//        for (int j = 0, m = students.size(); j < m; ++j) {
//            double redistribute = 0;
//
//            for (Map.Entry<Integer, Integer> overfiled : overFilledLabs.entrySet()) {
//
//                double reduceCoff = (((double) overfiled.getValue()) / maxi) * (UPPER_BOUND - LOWWER_BOUND) + LOWWER_BOUND;
//
//                redistribute += currentDistribution[overfiled.getKey()][j] * reduceCoff;
//
//                currentDistribution[overfiled.getKey()][j] *= (1 - reduceCoff);
//            }
//
//
//            double increment = redistribute / (labs.size() - overFilledLabs.size());
//            for (int i = 0, n = labs.size(); i < n; ++i) {
//                if (overFilledLabs.containsKey(i)) continue;
//
//                currentDistribution[i][j] += increment;
//            }
//        }
////
//        double[] control = new double[students.size()];
//        for (int j = 0, m = students.size(); j < m; ++j) {
//
//            for (int i = 0, n = labs.size(); i < n; ++i) {
//                control[j] += currentDistribution[i][j];
//
//            }
//        }
//
//        System.out.println(Arrays.toString(control));


    }


    private void update(double[][] newDistribution) {
        double[][] queueDistribution = queue.getDistribution();

        for (int i = 0, n = labs.size(); i < n; ++i) {
            for (int j = 0, m = students.size(); j < m; ++j) {
                currentDistribution[i][j] = (1.0 - smoothingParameter - queueFactor) * currentDistribution[i][j] +
                                                                  smoothingParameter * newDistribution[i][j]     +
                                                                         queueFactor * queueDistribution[i][j];
            }
        }
    }

    private double[][] evaluate(List<Solution> solutions) {
        double[][] newDistribution = new double[labs.size()][students.size()];

        solutions.sort((s1, s2) -> (Integer.compare(s1.getCost(), s2.getCost())));


        List<Solution> best = solutions.subList(0, Nb);


        if (best.get(0).getCost() < bestSolution.getCost()) {
            bestSolution = best.get(0);
        }

//        System.out.println(bestSolution.getCost());
//        System.out.println(best.get(0).getCost());


        for (int i = 0; i < queueSize; ++i) {
            queue.add(best.get(i));
        }



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
                cost += 80_000;
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
                cost += (5000 + overfillingCoff * Math.pow((filled[i] - labs.get(i).getMaxStudents()), 2));
            }

            if (filled[i] < labs.get(i).getMinStudents()) {
                cost += (5000 + overfillingCoff * Math.pow((labs.get(i).getMinStudents() - filled[i]), 2));
            }
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
//        if (stopFile.exists()) {
            stopFile.delete();
            int colissionCounter = 0;
            int[] solution = bestSolution.getSequnce();

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
            System.out.println("Score: " + bestSolution.getCost());

            writer.println("Ukupan broj kolizija: " + colissionCounter);
            writer.println("Ukupan broj prepunjenih: " + overfilled);
            writer.println("Score: " + bestSolution.getCost());

            writer.println("Smoothing parameter: " + smoothingParameter);
            writer.println("Sample size: " + sampleSize);
            writer.println("Best: " + Nb);

            writer.flush();

            return true;
//        }
//        return false;
    }


    private Result generateResult() {

        int colissionCounter = 0;
        int[] solution = bestSolution.getSequnce();

        for (int i = 0, n = students.size(); i < n; ++i) {

            students.get(i).setLab(labs.get(solution[i]));

            if (students.get(i).hasCollision()) {
                colissionCounter++;
            }
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
        }


        return new Result(sampleSize, Nb, smoothingParameter, queueSize, queueFactor, overfilled, colissionCounter, bestSolution.getCost(), overfillingCoff);
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



//    svakako all time best spremati
//    dodati priority queueue s najboljih X, trivijalno je sa 1 i od toga krenuti
//    smoothing parametar provjeriti utjecaj
//    balansiranje ne treba ovisiti o broju za koliko je termin prepunjen
//    vremenski promjenjiv utjecaj all best (queue)

//    1. uvod (1-2) (pisem na kraju cijelog rada) - struktura

//    2. Pregled podrucja (related work) - problemi rasporedjivanja (vrste), diskusija kako se mogu rjesavati (drugi optimizacijski algoritmi), izrada satnice, rasporeda ispita
//          - pregled optimizacijskih algoritama (mravlji, iterative sarch, temeljeni na procjenama distribucija
//          - kod vrsta problma, zadani termini
//    3. Opis ovog mojeg algoritma - detaljan opis (kao i paper), leading ones problem, moji rezultati
    // - mozda pokazati slicnost s mravljim algoritmom - pitaj cupica
//    4. Odarbani problem rasporedjivanja (labosi - diglog i oop), konkretan problem i ogranicanje, kako se racuna fitness (kazna)
//      - kako sam prilagodio algoritam na nas problem
//          sto sam koristio, koje ideje, inicijalna distribucija, queue, rebalansiranje
    //     - pitati cupica ima li smisla da ovo koje sam stvari dodavao pisem po principu kako sam zapoceo, sto nije valjalo, koju sam promjenu napravio
    // i kako je onda utjecala, ili da odmah napisem zavrsno stanje
//      - ideja samog algoritma (programsko rjesenje) - ne puno koda (pozvati se na pseudokodove iz prethodnih poglavlja)
//      - objasniti kako dodjeljujem kaznu
//      - objasniti queue
//    5. Ostvareni rezultati i eksperimentalno vrednovanje
//       - 6 problem (primjera)
//       - sa/bez heuristika
//       - parametri
//    6. zakljucak - pol stranice rekapitulacija
//                 - daljnji rad, trenutni problemi (paralelizacija svega toga da bi bilo brze)
//



    public static void main(String[] args) throws IOException {
        List<Lab> labs = parseLabs(         "./src/main/resources/primjeri/primjer6/termini.txt", true);
        List<Student> studs = parseStudents("./src/main/resources/primjeri/primjer6/zauzetost.csv");

        LabsModel model = new LabsModel(
                300, 50, 0.5, null, studs, labs, 10_000,
                50, 0.3
        );

        StopWatch timer = new StopWatch();

        timer.start();
        model.run();
        timer.stop();

//        System.out.println("Broj iteracija: " + i);
        System.out.println("Vrijeme: " + timer.getTime());
//
//        SolutionsQueue q = new SolutionsQueue(5, 5, 5);
//
//        q.add(new Solution(new int[]{1, 2, 3, 4, 0}, 50));
//        q.add(new Solution(new int[]{1, 2, 3, 4, 0}, 10));
//        q.add(new Solution(new int[]{1, 2, 3, 4, 0}, 30));
//        q.add(new Solution(new int[]{1, 2, 3, 4, 0}, 20));
//        q.add(new Solution(new int[]{1, 2, 3, 4, 0}, 10));
//        q.add(new Solution(new int[]{0, 0, 0, 0, 0}, 200));
//        q.add(new Solution(new int[]{0, 0, 0, 0, 0}, 60));
//        q.add(new Solution(new int[]{0, 0, 0, 0, 0}, 10));

//        double[][] d = q.getDistribution();
//
//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 5; ++j) {
//                System.out.print(" " + d[i][j] + " ");
//            }
//
//            System.out.println();
//        }
////
//        List<Solution> l = new ArrayList<>();
////
//        l.add(new Solution(new int[]{1, 2, 3, 4, 0}, 50));
//        l.add(new Solution(new int[]{1, 2, 3, 4, 0}, 40));
//        l.add(new Solution(new int[]{1, 2, 3, 4, 0}, 30));
//        l.add(new Solution(new int[]{1, 2, 3, 4, 0}, 20));
//        l.add(new Solution(new int[]{1, 2, 3, 4, 0}, 10));
//        l.add(new Solution(new int[]{0, 0, 0, 0, 0}, 5));
//
//
//        l.sort((s1, s2) -> (Integer.compare(s1.getCost(), s2.getCost())));
//
//        for (Solution s : l) {
//            System.out.println(s.getCost());
//        }


    }

}




















