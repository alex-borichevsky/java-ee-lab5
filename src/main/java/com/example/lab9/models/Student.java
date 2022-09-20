package com.example.lab9.models;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;


public class Student implements Serializable {
    private String name;
    private int group;
    private ArrayList<Mark> marks;

    private String id;

    public Student() {
        this.name = "";
        this.id = UUID.randomUUID().toString();
        this.marks = new ArrayList<>();
    }

    public Student(String name, int group) {
        this();
        this.name = name;
        this.group = group;
    }

    public Student(String id, String name, int group) {
        this(name, group);
        this.id = id;
    }

    public Student(Student o) {
        this.id = o.id;
        this.name = o.name;
        this.group = o.group;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public int getGroup() {return group;}

    public void setGroup(int group) {this.group = group;}

    public String getId() {return id;}

    public ArrayList<Mark> getMarks() {return marks;}

    public void setMarks(ArrayList<Mark> marks) {this.marks = marks;}

    public void addMark(Mark mark) {this.marks.add(mark);}

    @Override
    public String toString() {
        StringBuilder marksString = new StringBuilder();
        for (Mark mark : marks) {
            marksString.append(mark.shortToString());
        }

        return "Student{" +
                "id='" + id + '\'' +
                ",\tname='" + name + '\'' +
                ",\tgroup=" + group +
                ",\tmarks={" + marksString +
                "} }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Student student = (Student) o;
        return group == student.group && name.equals(student.name);
    }
}