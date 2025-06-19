package rejectjob;

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

@WebServlet("/admin/reject-job")
public class RejectjobServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String jobIdStr = request.getParameter("id");

        if (jobIdStr == null || !jobIdStr.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid job ID");
            return;
        }

        int jobId = Integer.parseInt(jobIdStr);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            String sql = "UPDATE jobs SET status = 'rejected' WHERE job_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, jobId);
            stmt.executeUpdate();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"message\":\"✅ Job rejected successfully!\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"message\":\"❌ Failed to reject job\"}");
            out.flush();
        }
    }
}