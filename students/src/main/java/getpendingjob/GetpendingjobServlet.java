package getpendingjob;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/admin/pending-jobs")
public class GetpendingjobServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            String sql = "SELECT j.*, r.company_name FROM jobs j JOIN recruiters r ON j.recruiter_id = r.recruiter_id WHERE j.status = 'pending'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            List<Map<String, Object>> jobList = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> job = new HashMap<>();
                job.put("job_id", rs.getObject("job_id", Integer.class));
                job.put("title", rs.getObject("title", String.class));
                job.put("description", rs.getObject("description", String.class));
                job.put("location", rs.getObject("location", String.class));
                job.put("company_name", rs.getObject("company_name", String.class));
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
                out.print("\"company_name\":\"" + job.get("company_name") + "\"");
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