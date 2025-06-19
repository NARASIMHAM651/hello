package jobpost;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/jobpost/JobpostServlet")
public class JobpostServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("recruiterEmail");

        if (email == null || !session.getAttribute("recruiterEmail").toString().contains("@")) {
            response.sendRedirect("recruiter_login.html");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            // Get recruiter ID
            PreparedStatement userStmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
            userStmt.setString(1, email);
            ResultSet rs = userStmt.executeQuery();

            if (!rs.next()) {
                response.sendRedirect("recruiter_login.html");
                return;
            }

            int recruiterId = rs.getInt("id");

            // Read form fields
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String location = request.getParameter("location");
            String stipendStr = request.getParameter("stipend");
            String deadline = request.getParameter("deadline");

            double stipend = 0;
            if (stipendStr != null && !stipendStr.isEmpty()) {
                stipend = Double.parseDouble(stipendStr);
            }

            // Insert into DB
            String sql = "INSERT INTO jobs(recruiter_id, title, description, location, stipend, deadline, status) VALUES (?, ?, ?, ?, ?, ?, 'pending')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, recruiterId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, location);
            stmt.setDouble(5, stipend);
            stmt.setString(6, deadline);
            stmt.executeUpdate();

            // Send JSON response back to frontend
            PrintWriter out = response.getWriter();
            out.print("{\"status\":\"success\", \"message\":\"✅ Job posted successfully! Awaiting admin approval\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.print("{\"status\":\"error\", \"message\":\"❌ Failed to post job\"}");
            out.flush();
        }
    }
}