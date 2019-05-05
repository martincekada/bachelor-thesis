package hr.fer.zemris.bachelors.cross_entropy.structures;


import com.opencsv.CSVReader;
import com.sun.xml.internal.bind.v2.TODO;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Util {
    public static DateFormat onlyDateFormat       = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat onlyTimeFormat       = new SimpleDateFormat("HH:mm:ss");
    public static DateFormat fullFormat           = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    public static SimpleDateFormat withoutSeconds = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public static List<Lab> parseLabs(String path, boolean skipCommented) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            List<Lab> labs = new ArrayList<>(lines.size());

            for (String line : lines) {
                if (skipCommented && line.startsWith("#") || line.isEmpty()) continue;

                String[] splited = line.split(";");
                String[] minMax = splited[1].split("/");


                labs.add(new Lab(splited[0], splited[2], splited[3], splited[4], minMax[0], minMax[1], splited[5]));

            }

            return labs;

        } catch(IOException e) {
            System.out.println("Could not read file: " + path);
        }

        return null;
    }

    public static List<Student> parseStudents(String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            CSVReader csvReader = new CSVReader(reader);
            List<Student> students = new ArrayList<>();

            String[] readLine;
            while ((readLine = csvReader.readNext()) != null) {
                String[] line = readLine[0].split(";");
//                if (skipCommented && line.startsWith("#") || line.isEmpty()) continue;
//                System.out.println(Arrays.toString(line));

                Student student = findOrCreateStudent(line[0], students);

//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


                Event time = new Event(withoutSeconds.parse(line[1] + " " + line[2]), withoutSeconds.parse(line[1] + " " + line[3]));

                student.addLecture(new Lecture(time));

                if (!students.contains(student)) students.add(student);

            }

            return students;

        } catch(IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

//    TODO: optimize
    public static Student findOrCreateStudent(String jmbag, List<Student> students) {
        Student stud = new Student(jmbag);
        if (students.contains(stud)) {
            return students.get(students.indexOf(stud));
        }

        return stud;
    }


    public static void main(String args[]) throws ParseException {
        Date now = new Date();
        Date notNow = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 09:00");
        Date n9 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 09:00");
        Date n10 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 10:00");
        Date n11 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 11:00");
        Date n12 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 12:00");
        Date n13 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 13:00");
        Date n14 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 14:00");
        Date n15 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 15:00");
        Date n16 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 16:00");
        Date n17 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("20-03-2019 17:00");


        Event e9_10 = new Event(n9, n10);
        Event e10_11 = new Event(n10, n11);
        Event e11_12 = new Event(n11, n12);
        Event e10_12 = new Event(n10, n12);
        Event e9_11 = new Event(n9, n11);
        Event e9_13 = new Event(n9, n13);
        Event e11_13 = new Event(n11, n13);
        Event e9_14 = new Event(n9, n14);
        Event e10_13 = new Event(n10, n13);

        SimpleDateFormat test = new SimpleDateFormat("dd-MM-yyyy");

        System.out.println(test.format(n9));
//        System.out.println(e9_10.collides(e10_11) + " false"); // false
//        System.out.println(e9_10.collides(e11_12) + " false"); // false
//        System.out.println(e9_11.collides(e9_10) + " true"); // true
//        System.out.println(e9_11.collides(e9_10) + " true"); // true
//        System.out.println(e10_12.collides(e9_11) + " true"); // true
//        System.out.println(e10_12.collides(e10_12) + " true"); // true
//        System.out.println(e9_14.collides(e10_12) + " true"); // true
//        System.out.println(e9_11.collides(e10_11) + " true"); // true
//        System.out.println(e9_11.collides(e11_12) + " false"); // false
//        System.out.println(e9_10.collides(e11_12) + " false"); // false
//        System.out.println(e9_11.collides(e10_13) + " true"); // true


//
//
//
//        System.out.println(now);
//        System.out.println(notNow);
//
//        System.out.println(event.toString());
//
//
//        Student stud = new Student("212321");
//
//        stud.addLecture(new Lecture(event));
//        stud.addLecture(new Lecture(event));
//        stud.addLecture(new Lecture(event));


//        System.out.println(stud);

//        for (Lab l : parseLabs("./src/main/resources/termini.txt", true)) {
//            System.out.println(l.toString());
//        }
//
//        for (Student s : parseStudents("./src/main/resources/zauzetost.csv")) {
//            System.out.println(s.toString());
//        }


        List<Lab> labs = parseLabs("./src/main/resources/termini.txt", true);
        List<Student> studs = parseStudents("./src/main/resources/zauzetost.csv");


        System.out.println(studs.get(1).toString());
        System.out.println("----------");

        studs.get(1).setLab(labs.get(1));
        System.out.println(studs.get(1).printLabDay());

        File tempFile = new File("./stop.txt");


        while(true) {
            if (tempFile.exists()) {
                System.out.println("Stvoren file!");
                break;
            }
        }

    }

}
