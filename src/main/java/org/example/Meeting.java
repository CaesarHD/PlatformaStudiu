package org.example;

import java.time.LocalDateTime;
import java.util.List;

public class Meeting {
    private int id;
    private String type;
    private int maxNb;
    private int crtNb;
    private String className;
    private int classId;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<Student> students;

    public Meeting() {

    }

    public Meeting(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Meeting(int id, String type, int maxNb, int crtNb, String className, int classId, String description, LocalDateTime startDate, LocalDateTime endDate, List<Student> students) {
        this.id = id;
        this.type = type;
        this.maxNb = maxNb;
        this.crtNb = crtNb;
        this.className = className;
        this.classId = classId;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.students = students;
    }

    public String updateMeeting() {
        return "UPDATE programari\n" +
                "SET \n" +
                "    tip_activitate = '" + this.type + "',\n" +
                "    data_inceput = '" + this.startDate + "',\n" +
                "    data_final = '" + this.endDate + "',\n" +
                "    nr_max_participanti = " + this.maxNb + ",\n" +
                "    descriere = '" + this.description + "'\n" +
                "WHERE \n" +
                "    id_activitate = " + this.id + ";";
    }

    public String selectStudentsandGrades() {
        return ("select distinct nume, prenume, CNP, nota from note_activitati join proiect.utilizatori on note_activitati.CNP_student = utilizatori.CNP where id_activitate = '" + this.id + "';");
    }

    public String selectClassId() {
        return ("SELECT materii.id\n" +
                "FROM materii\n" +
                "JOIN programari\n" +
                "ON materii.id = programari.id_materie\n" +
                "WHERE programari.id_activitate = '" + this.id + "';");
    }

    public String selectClassName() {
        return ("SELECT materii.nume\n" +
                "FROM materii\n" +
                "JOIN programari\n" +
                "ON materii.id = programari.id_materie\n" +
                "WHERE programari.id_programare = '" + this.id + "';");
    }


    public String deleteMeeting() {
        return "DELETE FROM programari WHERE id_activitate = " + this.id + ";";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMaxNb() {
        return maxNb;
    }

    public void setMaxNb(int maxNb) {
        this.maxNb = maxNb;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getCrtNb() {
        return crtNb;
    }

    public void setCrtNb(int crtNb) {
        this.crtNb = crtNb;
    }
}
