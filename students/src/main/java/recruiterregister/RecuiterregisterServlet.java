package recruiterregister;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;



import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;

@WebServlet("/recruiter-register")
public class RecuiterregisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String companyName = request.getParameter("company");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            response.sendRedirect("recruiter_register.html?error=Passwords+do+not+match");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            // Step 1: Insert into users table
            String userSql = "INSERT INTO users(email, password, role) VALUES (?, ?, 'recruiter')";
            PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, email);
            userStmt.setString(2, password); // Later: hash it using BCrypt
            int rowsInserted = userStmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet rs = userStmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);

                    // Step 2: Insert into recruiters table
                    String recruiterSql = "INSERT INTO recruiters(recruiter_id, company_name, contact_person, mobile) VALUES (?, ?, ?, ?)";
                    PreparedStatement recruiterStmt = conn.prepareStatement(recruiterSql);
                    recruiterStmt.setInt(1, userId);
                    recruiterStmt.setString(2, companyName);
                    recruiterStmt.setString(3, "N/A"); // Optional field
                    recruiterStmt.setString(4, "N/A"); // Optional field
                    recruiterStmt.executeUpdate();

                    // Redirect to dashboard
                    response.sendRedirect("recruiter_login.html?name=" + companyName);
                }
            } else {
                response.sendRedirect("recruiter_register.html?error=Registration failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("recruiter_register.html?error=Database error");
        }
    }
}