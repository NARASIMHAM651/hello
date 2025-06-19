package recruiterjob;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/recruiter/jobs")
public class GetRecruiterJobsServlet extends HttpServlet {
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
            String sql = "SELECT * FROM jobs WHERE recruiter_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, recruiterId);
            ResultSet jobRs = stmt.executeQuery();

            List<Map<String, Object>> jobList = new ArrayList<>();

            while (jobRs.next()) {
                Map<String, Object> job = new HashMap<>();
                job.put("job_id", jobRs.getObject("job_id", Integer.class));
                job.put("title", jobRs.getObject("title", String.class));
                job.put("description", jobRs.getObject("description", String.class));
                job.put("location", jobRs.getObject("location", String.class));
                job.put("status", jobRs.getObject("status", String.class));
                job.put("posted_at", jobRs.getObject("posted_at", String.class));
                jobList.add(job);
            }

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            out.print("[");
            for (int i = 0; i < jobList.size(); i++) {
                Map<String, Object> job = jobList.get(i);
                out.print("{");
                out.print("\"job_id\":" + job.get("job_id") + ",");
                out.print("\"title\":\"" + job.get("title") + "\",");
                out.print("\"description\":\"" + job.get("description") + "\",");
                out.print("\"location\":\"" + job.get("location") + "\",");
                out.print("\"status\":\"" + job.get("status") + "\"");
                out.print("}");

                if (i < jobList.size() - 1) {
                    out.print(",");
                }
            }
            out.print("]");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load jobs");
        }
    }
}