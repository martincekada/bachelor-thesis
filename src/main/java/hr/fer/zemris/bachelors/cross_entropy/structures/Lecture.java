package hr.fer.zemris.bachelors.cross_entropy.structures;

import java.util.Date;

public class Lecture {
    private Event time;
    private String name;

    public Lecture(Date start, Date end, String name) {
        this.time = new Event(start, end);
        this.name = name;
    }

    public Lecture(Date start, Date end) {
        this.time = new Event(start, end);
    }

    public Lecture(Event event) {
        this.time = event;
    }

    public Event getTime() {
        return time;
    }

    @Override
    public String toString() {
        return time.toString();
    }
}
