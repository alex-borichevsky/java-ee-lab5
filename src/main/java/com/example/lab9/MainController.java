package com.example.lab9;

import com.example.lab9.database.dbDriver;
import com.example.lab9.models.Mark;
import com.example.lab9.models.Student;

import java.io.*;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@MultipartConfig
@WebServlet("/controller")
public class MainController extends HttpServlet {

    private dbDriver db;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("[main controller] init");
        db = new dbDriver();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doAction(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doAction(request, response);
    }

    private void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("status", "404");
            request.getRequestDispatcher("/").forward(request, response);
            return;
        }
        System.out.println("[main controller] " + action);
        switch (action) {
            case "getStudents":
                request.setAttribute("students", db.getStudents());
                break;
            case "getBadStudents":
                request.setAttribute("students", db.getStudents());
                request.setAttribute("badIds", db.getBadStudentsIds());
                for (String str : db.getBadStudentsIds()){
                    System.out.println(str);
                }
                break;
            case "addStudent":
                String name = request.getParameter("name");
                String groupStr = request.getParameter("group");
                if (name == null || groupStr == null) {
                    request.setAttribute("status", "500");
                }
                int group = Integer.parseInt(groupStr);
                Student s = new Student(name, group);
                db.createStudent(s);
                Map<String, String[]> params = request.getParameterMap();
                for (Map.Entry<String, String[]> entry : params.entrySet()) {
                    if (entry.getKey().equals("name") || entry.getKey().equals("group") || entry.getKey().equals("action")) {
                        continue;
                    }
                    System.out.println(entry.getKey() + " / " + entry.getValue());
                    int grade = Integer.parseInt(entry.getValue()[0]);
                    db.createMark(new Mark(entry.getKey(), grade, s.getId()));

                }
                request.setAttribute("students", db.getStudents());
                request.setAttribute("badIds", db.getBadStudentsIds());
                break;
            case "updateStudent":{
                String oldName = request.getParameter("oldName");
                System.out.println(oldName);
                String newName = request.getParameter("newName");
                System.out.println(newName);
                if (oldName == null || newName == null) {
                    request.setAttribute("status", "500");
                }
                db.updateStudent(oldName, newName);
            }
            case "deleteStudent":
                System.out.println("[main controller] im'here");
                String id = request.getParameter("id");
                System.out.println(id);
                if (id == null) {
                    request.setAttribute("status", "500");
                }
                db.deleteStudent(id);
                request.setAttribute("students", db.getStudents());
                request.setAttribute("badIds", db.getBadStudentsIds());
                break;
            default:
                request.setAttribute("status", "404");
                break;
        }
        if (request.getAttribute("status") == null) {
            request.setAttribute("status", "200");
        }
        System.out.println("[main controller] " + request.getAttribute("status"));
        request.getRequestDispatcher("/").forward(request, response);
    }
}