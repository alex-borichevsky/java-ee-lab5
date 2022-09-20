package com.example.lab9.models;

import java.io.Serializable;
import java.util.UUID;


public class Mark implements Serializable {
    private String id;
    private Subject subject;
    private int grade;

    private String studentId;

    public Mark() {}

    public Mark(Subject subject, int grade, String studentId) {
        this.subject = subject;
        this.studentId = studentId;
        this.grade = grade;
        this.id = UUID.randomUUID().toString();

    }

    public Mark(String subject, int grade, String studentId) {
        this(Subject.valueOf(subject), grade, studentId);
    }

    public Subject getSubject() {return subject;}

    public String getStudentId() {return studentId;}

    public int getGrade() {return grade;}

    public String getId() {return id;}

    @Override
    public String toString() {
        return "Mark{" +
                "studentId='" + studentId + '\'' +
                ",\tsubject=" + subject +
                ",\tgrade=" + grade +
                '}';
    }

    public String shortToString() {
        return '{' +
                "subject=" + subject +
                ",\tgrade=" + grade +
                "} ";
    }

    public String formatted() {
        return subject + " : " + grade + " / ";
    }
}