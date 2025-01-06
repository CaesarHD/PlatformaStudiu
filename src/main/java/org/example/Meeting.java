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
    private int professorActivityId;
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

    public String updateMeeting() {
        return "UPDATE programari\n" +
                "SET \n" +
                "    data_inceput = '" + this.startDate + "',\n" +
                "    data_final = '" + this.endDate + "', \n" +
                "    descriere_programare = '" + this.description + "' " +
                "WHERE \n" +
                "    id_programare = " + this.id + ";";
    }

    public String selectClassName() {
        return ("SELECT materii.nume\n" +
                "FROM materii\n" +
                "join activitati_profesori ap on materii.id = ap.id_materie\n" +
                "join programari p on ap.id_activitate = p.id_activitate " +
                "WHERE id_programare = '" + this.id + "';");
    }


    public String deleteMeeting() {
        return "DELETE FROM programari WHERE id_programare = " + this.id + ";";
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

    public int getProfessorActivityId() {
        return professorActivityId;
    }

    public void setProfessorActivityId(int professorActivityId) {
        this.professorActivityId = professorActivityId;
    }
}
