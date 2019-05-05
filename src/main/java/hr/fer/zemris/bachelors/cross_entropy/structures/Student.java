package hr.fer.zemris.bachelors.cross_entropy.structures;

import java.util.*;

public class Student {
    private List<Lecture> lectures = new ArrayList<>();
    private String jmbag;
    private Lab lab;
    private Map<String, List<Lecture>> lecturesByDate;
    private List<Long> starts = new ArrayList<>();
    private List<Long> ends = new ArrayList<>();

    public Student(String jmbag) {
        this.jmbag = jmbag;
    }

    public boolean addLecture(Lecture l) {
        return lectures.add(l);
    }



    public boolean increasesDayDuration(Lab newLab) {
        if (lecturesByDate == null) {
            initLecturesByDate();
        }

        String labDay = Util.onlyDateFormat.format(newLab.getTime().getStart());

        if (!lecturesByDate.containsKey(labDay)) {
            return true;
        }


        starts.clear();
        ends.clear();

        for (Lecture lecture : lecturesByDate.get(labDay)) {
            starts.add(lecture.getTime().getStart().getTime());
            ends.add(lecture.getTime().getEnd().getTime());
        }

        return newLab.getTime().getStart().getTime() < Collections.min(starts) ||
                newLab.getTime().getEnd().getTime() > Collections.max(ends);
    }

    public boolean onFreeDay(Lab newLab) {
        if (lecturesByDate == null) {
            initLecturesByDate();
        }

        String labDay = Util.onlyDateFormat.format(newLab.getTime().getStart());

        if (!lecturesByDate.containsKey(labDay)) {
            return true;
        }

        return false;
    }


    private void initLecturesByDate() {
        lecturesByDate = new HashMap<>();


        for (Lecture l : lectures) {
            String lectureDate = Util.onlyDateFormat.format(l.getTime().getStart());
            if (!lecturesByDate.containsKey(lectureDate)) {
                lecturesByDate.put(lectureDate, new ArrayList<>());
            }

            lecturesByDate.get(lectureDate).add(l);

        }

    }


    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public String printLabDay() {
        if (lab == null) return "Studentu nije pridruzen termin labosa";


        List<String> sj = new ArrayList<>();

        sj.add(lab.toString());


        String labDay = Util.onlyDateFormat.format(lab.getTime().getStart());
        for (Lecture l : lectures) {
            if (Util.onlyDateFormat.format(l.getTime().getStart()).equals(labDay)) {
                sj.add(l.toString());
            }
        }

        Collections.sort(sj);
        StringJoiner st = new StringJoiner("\n");

        st.add(this.jmbag);

        for (String s : sj) {
            st.add(s);
        }



        return st.toString();
    }

    public boolean hasCollision() {
        return hasCollisionWith(lab);
    }

    public boolean hasCollisionWith(Lab labos) {
        if (labos == null) throw new NullPointerException("Nije predan labos!!");
        if (lecturesByDate == null) {
            initLecturesByDate();
        }


        String labDay = Util.onlyDateFormat.format(labos.getTime().getStart());

        if (!lecturesByDate.containsKey(labDay)) {
            return false;
        }


        for (Lecture lecture : lecturesByDate.get(labDay)) {
            if (lecture.getTime().collides(labos.getTime())) {
                return true;
            }

        }


        return false;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" | ");

        joiner.add(jmbag);

        for (Lecture l : lectures) {
            joiner.add(l.toString());
        }

        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return jmbag.equals(student.jmbag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jmbag);
    }
}
