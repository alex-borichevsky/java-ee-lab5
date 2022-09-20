package com.example.lab9.database;



import com.example.lab9.config.Options;
import com.example.lab9.models.Mark;
import com.example.lab9.models.Student;
import com.example.lab9.models.Subject;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class dbDriver {
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private ResultSet res = null;
    private String sql;

    private static String StudentsTable = Student.class.getSimpleName();
    private static String MarksTable = Mark.class.getSimpleName();

    private static final String CREATE_STUDENT = MessageFormat.format(
            "INSERT INTO {0} (id, s_name, s_group) VALUES(?, ?, ?);", StudentsTable);

    private static final String CREATE_MARK = MessageFormat.format(
            "INSERT INTO {0} (id, subject, grade, studentId) VALUES(?, ?, ?, ?);", MarksTable);

    private static final String GET_STUDENTS = MessageFormat.format(
            "SELECT * FROM {0} INNER JOIN {1} ON {1}.studentId={0}.id;", StudentsTable, MarksTable);

    private static final String GET_BAD_STUDENTS = MessageFormat.format(
            "SELECT {0}.id, {0}.s_name, COUNT(*) as bad_marks_count FROM {0}" +
                    " INNER JOIN {1} ON {0}.id={1}.studentId" +
                    " WHERE {1}.grade < 4 GROUP BY {0}.id" +
                    " HAVING COUNT(*) > 2;", StudentsTable, MarksTable);

    private static final String DELETE_STUDENT = MessageFormat.format(
            "DELETE st, mrk FROM {0} st" +
                    " INNER JOIN {1} mrk ON st.id=mrk.studentId" +
                    " WHERE st.id=?;", StudentsTable, MarksTable);


    public dbDriver() {
        connect();
    }

    private void connect() {
        try {
            Class.forName(Options.JDBC_DRIVER);
            System.out.println("[dbDriver] Connecting to database...");
            conn = DriverManager.getConnection(Options.DB_URL + Options.DB_NAME, Options.DB_USER, Options.DB_PASS);
            stmt = conn.createStatement();
            System.out.println("[dbDriver] Successfully connect to " + conn.getMetaData().getURL());
        } catch (Exception se) {
            System.out.println("[dbDriver] Some problem with db connection");
            se.printStackTrace();
            System.exit(1);
        }
    }
    public void updateStudent(String oldName, String newName) {
        try{
            pstmt = conn.prepareStatement("update student set s_name = ? where s_name= ?");
            pstmt.setString(1, newName);
            pstmt.setString(2, oldName);
            pstmt.executeUpdate();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    public void createStudent(Student student) {
        try {
            pstmt = conn.prepareStatement(CREATE_STUDENT);
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setInt(3, student.getGroup());
            pstmt.executeUpdate();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }
    public void createMark(Mark mark) {
        try {
            stmt = conn.createStatement();
            pstmt = conn.prepareStatement(CREATE_MARK);
            pstmt.setString(1, mark.getId());
            pstmt.setString(2, mark.getSubject().toString());
            pstmt.setInt(3, mark.getGrade());
            pstmt.setString(4, mark.getStudentId());
            pstmt.executeUpdate();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

//    public void createStudentsTable() {
//        String sql = "CREATE TABLE " + StudentsTable +
//                "(id VARCHAR(255) not NULL, " +
//                " s_name VARCHAR(255), " +
//                " s_group INTEGER, " +
//                " PRIMARY KEY ( id ))";
//        createTable(Student.class.getSimpleName(), sql);
//    }
//
//    private void createMarkTable() {
//        String sql = "CREATE TABLE " + MarksTable +
//                "(id VARCHAR(255) not NULL, " +
//                " subject VARCHAR(255), " +
//                " grade INTEGER, " +
//                " studentId VARCHAR(255) not NULL, " +
//                " PRIMARY KEY ( id ))";
//        createTable(Mark.class.getSimpleName(), sql);
//    }

    public void deleteStudent(String id) {
        try {
            System.out.println("[dbDriver] Delete student...");
            pstmt = conn.prepareStatement(DELETE_STUDENT);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            System.out.println("[dbDriver] Successfully delete student with id=" + id);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public ArrayList<Student> getStudents() {
        System.out.println("[dbDriver] Select students...");
        ArrayList<Student> data = new ArrayList<>();
        try {
            res = stmt.executeQuery(GET_STUDENTS);
            HashMap<String, Student> map = new HashMap<>();
            while (res.next()) {
                String id = res.getString("id");
                Student st = map.get(id);
                if (st == null) {
                    st = new Student(id, res.getString("s_name"), Integer.parseInt(res.getString("s_group")));
                }

                st.addMark(new Mark(res.getString("subject"), Integer.parseInt(res.getString("grade")), id));
                map.put(id, st);
                if (Options.DEBUG) {
                    System.out.println("\t#" + res.getRow()
                            + "\t" + res.getString("s_name")
                            + "\t" + res.getString("subject")
                            + "\t" + res.getString("grade")
                            + "\t" + res.getString("id"));
                }
            }

            for (Map.Entry<String, Student> entry : map.entrySet()) {
                Student st = entry.getValue();
                data.add(st);
                if (Options.DEBUG) {
                    System.out.println(st);
                }
            }
            System.out.println("[dbDriver] Successfully select " + data.size() + " students.");

        } catch (Exception se) {
            se.printStackTrace();
        }
        return data;
    }

    public ArrayList<String> getBadStudentsIds() {
        System.out.println("[dbDriver] Select bad students...");
        ArrayList<String> data = new ArrayList<>();
        try {
            res = stmt.executeQuery(GET_BAD_STUDENTS);
            while (res.next()) {
                data.add(res.getString("id"));
                if (Options.DEBUG) {
                    System.out.println("\t#" + res.getRow()
                            + "\t" + res.getString("s_name")
                            + "\t" + res.getString("bad_marks_count")
                            + "\t" + res.getString("id"));
                }
            }
            System.out.println("[dbDriver] Successfully select " + data.size() + " bad students.");
        } catch (Exception se) {
            se.printStackTrace();
        }
        return data;
    }
}