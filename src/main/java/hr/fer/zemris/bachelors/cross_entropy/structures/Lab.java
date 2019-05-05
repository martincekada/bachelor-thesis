package hr.fer.zemris.bachelors.cross_entropy.structures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringJoiner;

public class Lab {
    private Event time;
    private int minStudents;
    private int maxStudents;
    private String place;
    String name;



    public Lab(String name, Event time, int minStudents, int maxStudents, String place) {
        this.name = name;
        this.time = time;
        this.minStudents = minStudents;
        this.maxStudents = maxStudents;
        this.place = place;
    }

    public Lab(String name, String date, String start, String end, String maxStudents, String minStudents, String place) {
        this.name = name;
        this.minStudents = Integer.valueOf(minStudents);
        this.maxStudents = Integer.valueOf(maxStudents);
        this.place = place;

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            this.time = new Event(Util.withoutSeconds.parse(date + " " + start), Util.withoutSeconds.parse(date + " " + end));
        } catch (ParseException e) {
            System.out.println("Could not parse: " + date + "; " + start + "; " + end);
        }
    }

    public Event getTime() {
        return time;
    }

    public int getMinStudents() {
        return minStudents;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("; ");
        joiner.add(time.toString());
        joiner.add(String.valueOf(minStudents));
        joiner.add(String.valueOf(maxStudents));
        joiner.add(String.valueOf(place));

        return joiner.toString();
    }
}
