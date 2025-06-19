package application;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/student/apply-job")
public class StudentapplicationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        String studentEmail = (String) session.getAttribute("studentEmail");

        if (studentEmail == null || !studentEmail.contains("@")) {
            response.sendRedirect("student_login.html");
            return;
        }

        String jobIdStr = request.getParameter("job_id");

        if (jobIdStr == null || !jobIdStr.matches("\\d+")) {
            response.sendRedirect("student_dashboard.html?error=Invalid+job+ID");
            return;
        }

        int jobId = Integer.parseInt(jobIdStr);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            // Get student ID
            PreparedStatement userStmt = conn.prepareStatement("SELECT student_id FROM students WHERE email = ?");
            userStmt.setString(1, studentEmail);
            ResultSet rs = userStmt.executeQuery();

            if (!rs.next()) {
                response.sendRedirect("student_dashboard.html?error=Student+not+found");
                return;
            }

            int studentId = rs.getInt("student_id");

            // Insert into applications table
            String sql = "INSERT INTO applications(student_id, job_id, status) VALUES (?, ?, 'Applied')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, jobId);
            stmt.executeUpdate();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"status\":\"success\", \"message\":\"✅ Applied successfully!\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"status\":\"error\", \"message\":\"❌ Failed to apply\"}");
            out.flush();
        }
    }
}