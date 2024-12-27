package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProfessorActivity {
    private int id;
    private String type;
    private int maxNb;
    private String className;
    private int classId;
    private String description;
    private List<Student> students;

    private Map<Student, Integer> grades;

    public ProfessorActivity() {
        students = new ArrayList<>();
        grades = new HashMap<>();
    }

    public ProfessorActivity(int id, LocalDateTime startDate, LocalDateTime endDate, String type, int maxNb, String description) {
        this.id = id;
        this.type = type;
        this.maxNb = maxNb;
        this.description = description;
        students = new ArrayList<>();
        grades = new HashMap<>();
    }



    public String changeGrade(Student student) {
        return ("UPDATE note_activitati set nota = '" + this.getGrades().get(student) + "' where CNP_student = '" + student.getCNP() + "';");

    }

    public String selectStudentsandGrades() {
        return ("select distinct nume, prenume, CNP, nota from note_activitati join proiect.utilizatori on note_activitati.CNP_student = utilizatori.CNP where id_activitate = '" + this.id + "';");
    }

    public String selectClassName() {
        return ("SELECT materii.nume\n" +
                "FROM materii\n" +
                "JOIN activitati_profesori\n" +
                "ON materii.id = activitati_profesori.id_materie\n" +
                "WHERE activitati_profesori.id_activitate = '" + this.id + "';");
    }

    public String selectClassId() {
        return ("SELECT materii.id\n" +
                "FROM materii\n" +
                "JOIN activitati_profesori\n" +
                "ON materii.id = activitati_profesori.id_materie\n" +
                "WHERE activitati_profesori.id_activitate = '" + this.id + "';");
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Map<Student, Integer> getGrades() {
        return grades;
    }

    public void setGrades(Map<Student, Integer> grades) {
        this.grades = grades;
    }

    @Override
    public String toString() {
        return "ProfessorActivity{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", maxNb=" + maxNb +
                ", className='" + className + '\'' +
                ", classId=" + classId +
                ", description='" + description + '\'' +
                '}';
    }
}
