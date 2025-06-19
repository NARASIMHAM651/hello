package resumepath;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;



import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/resume-path")
public class ResumepathServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String studentEmail = request.getParameter("email");

        // If no email in URL, try getting from session
        if (studentEmail == null || studentEmail.isEmpty()) {
            studentEmail = (String) session.getAttribute("studentEmail");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            PreparedStatement stmt = conn.prepareStatement(
                "SELECT resume_path FROM students WHERE student_id = (SELECT id FROM users WHERE email = ?)");
            stmt.setString(1, studentEmail);

            ResultSet rs = stmt.executeQuery();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            if (rs.next()) {
                String resumePath = rs.getString("resume_path");
                out.print("{\"resume_path\":\"" + resumePath + "\"}");
            } else {
                out.print("{}"); // No resume found
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load resume path");
        }
    }
}