package students;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;



import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

@WebServlet("/StudentrgisterServlet")
public class StudentrgisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String mobile = request.getParameter("mobile");

        // Optional: Confirm password match
        String confirmPassword = request.getParameter("confirm_password");
        if (!password.equals(confirmPassword)) {
            response.sendRedirect("student_register.html?error=Passwords+do+not+match");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            // Step 1: Insert into users table
            String userSql = "INSERT INTO users(email, password, role) VALUES (?, ?, 'student')";
            PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, email);
            userStmt.setString(2, password); // Later: hash it with BCrypt
            int rowsInserted = userStmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet rs = userStmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);

                    // Step 2: Insert into students table
                    String studentSql = "INSERT INTO students(student_id, full_name, mobile) VALUES (?, ?, ?)";
                    PreparedStatement studentStmt = conn.prepareStatement(studentSql);
                    studentStmt.setInt(1, userId);
                    studentStmt.setString(2, fullName);
                    studentStmt.setString(3, mobile);
                    studentStmt.executeUpdate();

                    // Redirect to dashboard
                    response.sendRedirect("student_login.html?success=Registration+successful");
                }
            } else {
                response.sendRedirect("student_register.html?error=Registration failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student_register.html?error=Database error");
        }
    }
}