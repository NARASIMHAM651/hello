package jobapprove;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/admin/approve-job")
public class JobapprovalServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String jobIdStr = request.getParameter("id");
        String action = request.getParameter("action");

        if (jobIdStr == null || action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        int jobId = Integer.parseInt(jobIdStr);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            String sql = "UPDATE jobs SET status = ? WHERE job_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, action); // approved / rejected
            stmt.setInt(2, jobId);
            stmt.executeUpdate();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"status\":\"success\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
            out.flush();
        }
    }
}