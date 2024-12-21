package org.example;

public class Subject {
    private int id;
    private String name;
    private int labWeight;
    private int semWeight;
    private int classWeight;

    public Subject() {
    }

    public Subject(int id, String name, int labWeight, int semWeight, int classWeight) {
        this.id = id;
        this.name = name;
        this.labWeight = labWeight;
        this.semWeight = semWeight;
        this.classWeight = classWeight;
    }

    public static String getSubject(int id) {
        return ("select * from materii where id = '" + id + "';" );
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

    public int getLabWeight() {
        return labWeight;
    }

    public void setLabWeight(int labWeight) {
        this.labWeight = labWeight;
    }

    public int getSemWeight() {
        return semWeight;
    }

    public void setSemWeight(int semWeight) {
        this.semWeight = semWeight;
    }

    public int getClassWeight() {
        return classWeight;
    }

    public void setClassWeight(int classWeight) {
        this.classWeight = classWeight;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", labWeight=" + labWeight +
                ", semWeight=" + semWeight +
                ", classWeight=" + classWeight +
                '}' + '\n';
    }
}
