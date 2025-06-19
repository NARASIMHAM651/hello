package adminlogin;

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

@WebServlet("/admin-login")
public class AdminloginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            // Query users table
            String sql = "SELECT * FROM users WHERE email=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                String role = rs.getString("role");

                if ("admin".equals(role) && dbPassword.equals(password)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("adminEmail", email);
                    session.setAttribute("adminName", "Admin");

                    // Redirect to dashboard
                    response.sendRedirect("admin_dashboard.html?name=admin");
                } else {
                    response.sendRedirect("admin_login.html?error=Invalid+credentials");
                }
            } else {
                response.sendRedirect("admin_login.html?error=Invalid+credentials");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin_login.html?error=Server+error");
        }
    }
}