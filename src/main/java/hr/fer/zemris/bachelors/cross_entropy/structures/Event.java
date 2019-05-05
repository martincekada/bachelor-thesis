package hr.fer.zemris.bachelors.cross_entropy.structures;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    private Date start;
    private Date end;


    public Event(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public boolean collides(Event other) {

        return Math.max(this.start.getTime(), other.start.getTime()) < Math.min(this.end.getTime(), other.end.getTime());
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return Util.onlyDateFormat.format(start) + ": <" + Util.onlyTimeFormat.format(start) + ", " + Util.onlyTimeFormat.format(end) + ">";
    }
}
