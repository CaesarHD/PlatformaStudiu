package org.example;

public class Course {
    private int id;
    private String name;
    private int credits;
    private String professorCNP;

    public Course(int id, String name, int credits, String professorCNP) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.professorCNP = professorCNP;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getProfessorCNP() {
        return professorCNP;
    }

    public void setProfessorCNP(String professorCNP) {
        this.professorCNP = professorCNP;
    }

    @Override
    public String toString() {
        return "Course ID: " + id +
                ", Name: " + name +
                ", Credits: " + credits +
                ", Professor CNP: " + professorCNP;
    }
}

