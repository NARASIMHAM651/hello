package recruiterlogin;

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

@WebServlet("/recruiter-login")
public class RecruiterloginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            // Query users table with join to recruiters table
            String sql = "SELECT u.*, r.company_name FROM users u JOIN recruiters r ON u.id = r.recruiter_id WHERE u.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                String role = rs.getString("role");
                String companyName = rs.getString("company_name");

                if ("recruiter".equals(role) && dbPassword.equals(password)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("recruiterEmail", email);
                    session.setAttribute("recruiterName", companyName);

                    // Redirect to dashboard
                    response.sendRedirect("recruiter_dashboard.html?name=" + companyName);
                } else {
                    response.sendRedirect("recruiter_login.html?error=Invalid+credentials");
                }
            } else {
                response.sendRedirect("recruiter_login.html?error=Invalid+credentials");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("recruiter_login.html?error=Server+error");
        }
    }
}