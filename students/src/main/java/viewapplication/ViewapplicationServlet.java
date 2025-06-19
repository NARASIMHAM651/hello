package viewapplication;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/recruiter/applications")
public class ViewapplicationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("email");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            // Get recruiter ID
            PreparedStatement userStmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
            userStmt.setString(1, email);
            ResultSet rs = userStmt.executeQuery();

            if (!rs.next()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Recruiter not found");
                return;
            }

            int recruiterId = rs.getInt("id");

            // Get all jobs posted by this recruiter
            PreparedStatement jobStmt = conn.prepareStatement("SELECT job_id FROM jobs WHERE recruiter_id = ?");
            jobStmt.setInt(1, recruiterId);
            ResultSet jobRs = jobStmt.executeQuery();

            List<Integer> jobIds = new ArrayList<>();
            while (jobRs.next()) {
                jobIds.add(jobRs.getInt("job_id"));
            }

            if (jobIds.isEmpty()) {
                response.getWriter().print("[]");
                return;
            }

            // Get all applications for these jobs
            String appSql = "SELECT * FROM applications WHERE job_id IN (" + String.join(",", Collections.nCopies(jobIds.size(), "?")) + ")";
            PreparedStatement appStmt = conn.prepareStatement(appSql);

            for (int i = 0; i < jobIds.size(); i++) {
                appStmt.setInt(i + 1, jobIds.get(i));
            }

            ResultSet appRs = appStmt.executeQuery();

            List<Map<String, Object>> apps = new ArrayList<>();

            while (appRs.next()) {
                Map<String, Object> app = new HashMap<>();
                app.put("application_id", appRs.getObject("application_id", Integer.class));
                app.put("student_id", appRs.getObject("student_id", Integer.class));
                app.put("application_status", appRs.getObject("status", String.class));

                // Get student name
                int studentId = appRs.getInt("student_id");
                PreparedStatement studentStmt = conn.prepareStatement("SELECT full_name FROM students WHERE student_id = ?");
                studentStmt.setInt(1, studentId);
                ResultSet studentRs = studentStmt.executeQuery();
                if (studentRs.next()) {
                    app.put("student_name", studentRs.getString("full_name"));
                }

                apps.add(app);
            }

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            out.print("[");
            for (int i = 0; i < apps.size(); i++) {
                Map<String, Object> app = apps.get(i);
                out.print("{");
                out.print("\"application_id\":" + app.get("application_id") + ",");
                out.print("\"student_name\":\"" + app.get("student_name") + "\",");
                out.print("\"application_status\":\"" + app.get("application_status") + "\"");
                out.print("}");

                if (i < apps.size() - 1) {
                    out.print(",");
                }
            }
            out.print("]");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load applications");
        }
    }
}